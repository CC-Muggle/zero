/**
 * Copyright © 2016-2023 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.solax.thingsboard.rule.engine.metadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNode;
import org.thingsboard.rule.engine.api.TbNodeConfiguration;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.*;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.thingsboard.common.util.DonAsynchron.withCallback;
import static org.thingsboard.rule.engine.api.TbRelationTypes.FAILURE;
import static org.thingsboard.server.common.data.DataConstants.*;

public abstract class TbAbstractGetAttributesNode<C extends TbGetAttributesNodeConfiguration, T extends EntityId> implements TbNode {

    private static final String VALUE = "value";
    private static final String TS = "ts";

    protected C config;
    private boolean fetchToData;
    private boolean isTellFailureIfAbsent;
    private boolean getLatestValueWithTs;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = loadGetAttributesNodeConfig(configuration);
        this.fetchToData = config.isFetchToData();
        this.getLatestValueWithTs = config.isGetLatestValueWithTs();
        this.isTellFailureIfAbsent = BooleanUtils.toBooleanDefaultIfNull(this.config.isTellFailureIfAbsent(), true);
    }

    protected abstract C loadGetAttributesNodeConfig(TbNodeConfiguration configuration) throws TbNodeException;

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) throws TbNodeException {
        try {
            withCallback(
                    findEntityIdAsync(ctx, msg),
                    entityId -> safePutAttributes(ctx, msg, entityId),
                    t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
        } catch (Throwable th) {
            ctx.tellFailure(msg, th);
        }
    }

    protected abstract ListenableFuture<T> findEntityIdAsync(TbContext ctx, TbMsg msg);

    private void safePutAttributes(TbContext ctx, TbMsg msg, T entityId) {
        if (entityId == null || entityId.isNullUid()) {
            ctx.tellNext(msg, FAILURE);
            return;
        }
        JsonNode msgDataNode;
        if (fetchToData) {
            msgDataNode = JacksonUtil.toJsonNode(msg.getData());
            if (!msgDataNode.isObject()) {
                ctx.tellFailure(msg, new IllegalArgumentException("Msg body is not an object!"));
                return;
            }
        } else {
            msgDataNode = null;
        }
        ConcurrentHashMap<String, List<String>> failuresMap = new ConcurrentHashMap<>();
        ListenableFuture<List<Map<String, ? extends List<? extends KvEntry>>>> allFutures = Futures.allAsList(
                getLatestTelemetry(ctx, entityId, TbNodeUtils.processPatterns(config.getLatestTsKeyNames(), msg), failuresMap),
                getAttrAsync(ctx, entityId, CLIENT_SCOPE, TbNodeUtils.processPatterns(config.getClientAttributeNames(), msg), failuresMap),
                getAttrAsync(ctx, entityId, SHARED_SCOPE, TbNodeUtils.processPatterns(config.getSharedAttributeNames(), msg), failuresMap),
                getAttrAsync(ctx, entityId, SERVER_SCOPE, TbNodeUtils.processPatterns(config.getServerAttributeNames(), msg), failuresMap)
        );
        withCallback(allFutures, futuresList -> {
            TbMsgMetaData msgMetaData = msg.getMetaData().copy();
            futuresList.stream().filter(Objects::nonNull).forEach(kvEntriesMap -> {
                kvEntriesMap.forEach((keyScope, kvEntryList) -> {
                    String prefix = getPrefix(keyScope);
                    kvEntryList.forEach(kvEntry -> {
                        String key = prefix + kvEntry.getKey();
                        if (fetchToData) {
                            JacksonUtil.addKvEntry((ObjectNode) msgDataNode, kvEntry, key);
                        } else {
                            msgMetaData.putValue(key, kvEntry.getValueAsString());
                        }
                    });
                });
            });
            TbMsg outMsg = fetchToData ?
                    TbMsg.transformMsgData(msg, JacksonUtil.toString(msgDataNode)) :
                    TbMsg.transformMsg(msg, msgMetaData);
            if (failuresMap.isEmpty()) {
                ctx.tellSuccess(outMsg);
            } else {
                ctx.tellFailure(outMsg, reportFailures(failuresMap));
            }
        }, t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
    }

    private ListenableFuture<Map<String, List<AttributeKvEntry>>> getAttrAsync(TbContext ctx, EntityId entityId, String scope, List<String> keys, ConcurrentHashMap<String, List<String>> failuresMap) {
        if (CollectionUtils.isEmpty(keys)) {
            return Futures.immediateFuture(null);
        }
        ListenableFuture<List<AttributeKvEntry>> attributeKvEntryListFuture = ctx.getAttributesService().find(ctx.getTenantId(), entityId, scope, keys);
        return Futures.transform(attributeKvEntryListFuture, attributeKvEntryList -> {
            if (isTellFailureIfAbsent && attributeKvEntryList.size() != keys.size()) {
                getNotExistingKeys(attributeKvEntryList, keys).forEach(key -> computeFailuresMap(scope, failuresMap, key));
            }
            Map<String, List<AttributeKvEntry>> mapAttributeKvEntry = new HashMap<>();
            mapAttributeKvEntry.put(scope, attributeKvEntryList);
            return mapAttributeKvEntry;
        }, MoreExecutors.directExecutor());
    }

    private ListenableFuture<Map<String, List<TsKvEntry>>> getLatestTelemetry(TbContext ctx, EntityId entityId, List<String> keys, ConcurrentHashMap<String, List<String>> failuresMap) {
        if (CollectionUtils.isEmpty(keys)) {
            return Futures.immediateFuture(null);
        }
        ListenableFuture<List<TsKvEntry>> latestTelemetryFutures = ctx.getTimeseriesService().findLatest(ctx.getTenantId(), entityId, keys);
        return Futures.transform(latestTelemetryFutures, tsKvEntries -> {
            List<TsKvEntry> listTsKvEntry = new ArrayList<>();
            tsKvEntries.forEach(tsKvEntry -> {
                if (tsKvEntry.getValue() == null) {
                    if (isTellFailureIfAbsent) {
                        computeFailuresMap(LATEST_TS, failuresMap, tsKvEntry.getKey());
                    }
                } else if (getLatestValueWithTs) {
                    listTsKvEntry.add(getValueWithTs(tsKvEntry));
                } else {
                    listTsKvEntry.add(new BasicTsKvEntry(tsKvEntry.getTs(), tsKvEntry));
                }
            });
            Map<String, List<TsKvEntry>> mapTsKvEntry = new HashMap<>();
            mapTsKvEntry.put(LATEST_TS, listTsKvEntry);
            return mapTsKvEntry;
        }, MoreExecutors.directExecutor());
    }

    private TsKvEntry getValueWithTs(TsKvEntry tsKvEntry) {
        ObjectMapper mapper = fetchToData ? JacksonUtil.OBJECT_MAPPER : JacksonUtil.ALLOW_UNQUOTED_FIELD_NAMES_MAPPER;
        ObjectNode value = JacksonUtil.newObjectNode(mapper);
        value.put(TS, tsKvEntry.getTs());
        JacksonUtil.addKvEntry(value, tsKvEntry, VALUE, mapper);
        return new BasicTsKvEntry(tsKvEntry.getTs(), new JsonDataEntry(tsKvEntry.getKey(), value.toString()));
    }

    private String getPrefix(String scope) {
        String prefix = "";
        switch (scope) {
            case CLIENT_SCOPE:
                prefix = "cs_";
                break;
            case SHARED_SCOPE:
                prefix = "shared_";
                break;
            case SERVER_SCOPE:
                prefix = "ss_";
                break;
        }
        return prefix;
    }

    private List<String> getNotExistingKeys(List<AttributeKvEntry> existingAttributesKvEntry, List<String> allKeys) {
        List<String> existingKeys = existingAttributesKvEntry.stream().map(KvEntry::getKey).collect(Collectors.toList());
        return allKeys.stream().filter(key -> !existingKeys.contains(key)).collect(Collectors.toList());
    }

    private void computeFailuresMap(String scope, ConcurrentHashMap<String, List<String>> failuresMap, String key) {
        List<String> failures = failuresMap.computeIfAbsent(scope, k -> new ArrayList<>());
        failures.add(key);
    }

    private RuntimeException reportFailures(ConcurrentHashMap<String, List<String>> failuresMap) {
        StringBuilder errorMessage = new StringBuilder("The following attribute/telemetry keys is not present in the DB: ").append("\n");
        if (failuresMap.containsKey(CLIENT_SCOPE)) {
            errorMessage.append("\t").append("[" + CLIENT_SCOPE + "]:").append(failuresMap.get(CLIENT_SCOPE).toString()).append("\n");
        }
        if (failuresMap.containsKey(SERVER_SCOPE)) {
            errorMessage.append("\t").append("[" + SERVER_SCOPE + "]:").append(failuresMap.get(SERVER_SCOPE).toString()).append("\n");
        }
        if (failuresMap.containsKey(SHARED_SCOPE)) {
            errorMessage.append("\t").append("[" + SHARED_SCOPE + "]:").append(failuresMap.get(SHARED_SCOPE).toString()).append("\n");
        }
        if (failuresMap.containsKey(LATEST_TS)) {
            errorMessage.append("\t").append("[" + LATEST_TS + "]:").append(failuresMap.get(LATEST_TS).toString()).append("\n");
        }
        failuresMap.clear();
        return new RuntimeException(errorMessage.toString());
    }
}
