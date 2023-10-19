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
package com.solax.thingsboard.rule.engine.clickhouse;

import com.clickhouse.jdbc.ClickHouseDataSource;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.rule.RuleChainTypeEnum;
import org.thingsboard.server.common.msg.TbMsg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import static org.thingsboard.common.util.DonAsynchron.withCallback;

@Slf4j
@RuleNode(type = ComponentType.EXTERNAL,
        name = "save to ClickHouse table",
        configClazz = TbClickHouseNodeConfiguration.class,
        nodeDescription = "Node stores data from incoming Message payload to the ClickHouse database into the predefined custom table" +
                " that should have <b>cs_tb_</b> prefix, to avoid the data insertion to the common TB tables.<br>" +
                "<b>Note:</b> rule node can be used only for ClickHouse DB.",
        nodeDetails = "Administrator should set the custom table name without prefix: <b>cs_tb_</b>. <br>" +
                "Administrator can configure the mapping between the Message field names and Table columns name.<br>" +
                "<b>Note:</b>If the mapping key is <b>$entity_id</b>, that is identified by the Message Originator, then to the appropriate column name(mapping value) will be write the message originator id.<br><br>" +
                "If specified message field does not exist or is not a JSON Primitive, the outbound message will be routed via <b>failure</b> chain," +
                " otherwise, the message will be routed via <b>success</b> chain.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbExternalNodeClickHouseConfig",
        icon = "file_upload",
        ruleChainTypes = RuleChainTypeEnum.CORE)
public class TbClickHouseNode implements TbNode {

    private static final JsonParser parser = new JsonParser();
    private static final String ENTITY_ID = "$entityId";
    private static final String USER = "user";
    private static final String PASSWORD = "password";
    private TbClickHouseNodeConfiguration config;
    private ClickHouseDataSource dataSource;
    private Connection connection;
    private PreparedStatement saveStmt;
    private Map<String, String> fieldsMap;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        config = TbNodeUtils.convert(configuration, TbClickHouseNodeConfiguration.class);
        try {
            Properties properties = new Properties();
            properties.setProperty(USER,config.getUsername());
            properties.setProperty(PASSWORD,config.getPassword());
            dataSource = new ClickHouseDataSource(config.getJdbcUrl(),properties);
            connection = dataSource.getConnection();
            saveStmt = getSaveStmt();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to ClickHouse database",e);
        }
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        withCallback(executeAsyncWrite(msg, ctx), aVoid -> ctx.tellSuccess(msg), e -> ctx.tellFailure(msg, e), ctx.getDbCallbackExecutor());
    }

    @Override
    public void destroy() {
        try {
            if(null != saveStmt)saveStmt.close();
            if(null!= connection)connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private PreparedStatement getSaveStmt() {
        fieldsMap = config.getFieldsMapping();
        if (fieldsMap.isEmpty()) {
            throw new RuntimeException("Fields(key,value) map is empty!");
        } else {
            return prepareStatement(new ArrayList<>(fieldsMap.values()));
        }
    }

    private PreparedStatement prepareStatement(List<String> fieldsList) {
        try {
            return connection.prepareStatement(createQuery(fieldsList));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String createQuery(List<String> fieldsList) {
        int size = fieldsList.size();
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ")
                .append(config.getTableName())
                .append("(");
        for (String field : fieldsList) {
            query.append(field);
            if (fieldsList.get(size - 1).equals(field)) {
                query.append(")");
            } else {
                query.append(",");
            }
        }
        query.append(" VALUES(");
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                query.append("?)");
            } else {
                query.append("?, ");
            }
        }
        return query.toString();
    }

    
    private ListenableFuture<TbMsg> executeAsyncWrite(TbMsg msg, TbContext ctx) {
        return ctx.getExternalCallExecutor().executeAsync(() -> save(msg, ctx));
    }
    private TbMsg save(TbMsg msg, TbContext ctx) {
        JsonElement data = parser.parse(msg.getData());
        if (!data.isJsonObject()) {
            throw new IllegalStateException("Invalid message structure, it is not a JSON Object:" + data);
        } else {
            JsonObject dataAsObject = data.getAsJsonObject();
            AtomicInteger i = new AtomicInteger(1);
            try {
                fieldsMap.forEach((key, value) -> {
                        if (key.equals(ENTITY_ID)) {
                            try {
                                saveStmt.setString(i.get(), msg.getOriginator().getId().toString());
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (dataAsObject.has(key)) {
                            try {
                                JsonElement dataKeyElement = dataAsObject.get(key);
                                if (dataKeyElement.isJsonPrimitive()) {
                                    JsonPrimitive primitive = dataKeyElement.getAsJsonPrimitive();
                                    if (primitive.isNumber()) {
                                        if (primitive.getAsString().contains(".")) {
                                            saveStmt.setDouble(i.get(), primitive.getAsDouble());
                                        } else {
                                            saveStmt.setLong(i.get(), primitive.getAsLong());
                                        }
                                    } else if (primitive.isBoolean()) {
                                        saveStmt.setBoolean(i.get(), primitive.getAsBoolean());
                                    } else if (primitive.isString()) {
                                        saveStmt.setString(i.get(), primitive.getAsString());
                                    } else {
                                        saveStmt.setNull(i.get(), Types.NULL);
                                    }
                                } else if (dataKeyElement.isJsonObject()) {
                                    saveStmt.setString(i.get(), dataKeyElement.getAsJsonObject().toString());
                                } else {
                                    throw new IllegalStateException("Message data key: '" + key + "' with value: '" + value + "' is not a JSON Object or JSON Primitive!");
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            throw new RuntimeException("Message data doesn't contain key: " + "'" + key + "'!");
                        }
                    i.getAndIncrement();
                });
                saveStmt.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return msg;
        }
    }
    
    
}
