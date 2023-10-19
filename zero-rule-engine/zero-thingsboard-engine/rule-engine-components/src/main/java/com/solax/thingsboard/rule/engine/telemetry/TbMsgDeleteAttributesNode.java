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
package com.solax.thingsboard.rule.engine.telemetry;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.StringUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.thingsboard.server.common.data.DataConstants.*;

@Slf4j
@RuleNode(
        type = ComponentType.ACTION,
        name = "delete attributes",
        configClazz = TbMsgDeleteAttributesNodeConfiguration.class,
        nodeDescription = "Delete attributes for Message Originator.",
        nodeDetails = "Attempt to remove attributes by selected keys. If msg originator doesn't have an attribute with " +
                " a key selected in the configuration, it will be ignored. If delete operation is completed successfully, " +
                " rule node will send the \"Attributes Deleted\" event to the root chain of the message originator and " +
                " send the incoming message via <b>Success</b> chain, otherwise, <b>Failure</b> chain is used.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbActionNodeDeleteAttributesConfig",
        icon = "remove_circle"
)
public class TbMsgDeleteAttributesNode implements TbNode {

    private TbMsgDeleteAttributesNodeConfiguration config;
    private List<String> keys;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbMsgDeleteAttributesNodeConfiguration.class);
        this.keys = config.getKeys();
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) throws ExecutionException, InterruptedException, TbNodeException {
        List<String> keysToDelete = keys.stream()
                .map(keyPattern -> TbNodeUtils.processPattern(keyPattern, msg))
                .distinct()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        if (keysToDelete.isEmpty()) {
            ctx.tellSuccess(msg);
        } else {
            String scope = getScope(msg.getMetaData().getValue(SCOPE));
            ctx.getTelemetryService().deleteAndNotify(
                    ctx.getTenantId(),
                    msg.getOriginator(),
                    scope,
                    keysToDelete,
                    checkNotifyDevice(msg.getMetaData().getValue(NOTIFY_DEVICE_METADATA_KEY), scope),
                    config.isSendAttributesDeletedNotification() ?
                            new AttributesDeleteNodeCallback(ctx, msg, scope, keysToDelete) :
                            new TelemetryNodeCallback(ctx, msg)
            );
        }
    }

    private String getScope(String mdScopeValue) {
        if (StringUtils.isNotEmpty(mdScopeValue)) {
            return mdScopeValue;
        }
        return config.getScope();
    }

    private boolean checkNotifyDevice(String notifyDeviceMdValue, String scope) {
        return SHARED_SCOPE.equals(scope) && (config.isNotifyDevice() || Boolean.parseBoolean(notifyDeviceMdValue));
    }

}
