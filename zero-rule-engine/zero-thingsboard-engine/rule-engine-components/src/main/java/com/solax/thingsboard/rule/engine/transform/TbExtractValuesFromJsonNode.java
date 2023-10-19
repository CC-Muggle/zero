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
package com.solax.thingsboard.rule.engine.transform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.common.msg.TbMsgMetaData;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RuleNode(
        type = ComponentType.TRANSFORMATION,
        name = "extract values from json",
        configClazz = TbExtractValuesFromJsonNodeConfiguration.class,
        nodeDescription = "Extract msg data to the new key names selected in the key mapping.",
        nodeDetails = "If the key that is selected in the key mapping is missed in the selected msg source(data or metadata), it will be ignored." +
                " Returns transformed messages via <code>Success</code> chain",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "",
        icon = "find_replace"
)
public class TbExtractValuesFromJsonNode implements TbNode {

    private TbExtractValuesFromJsonNodeConfiguration config;
    private Map<String, String> fieldsMapping;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbExtractValuesFromJsonNodeConfiguration.class);
        this.fieldsMapping = config.getFieldsMapping();
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) throws ExecutionException, InterruptedException, TbNodeException {
        TbMsgMetaData metaData = msg.getMetaData();
        String data = msg.getData();
        boolean msgChanged = false;
            JsonNode dataNode = JacksonUtil.toJsonNode(msg.getData());
            if (dataNode.isObject()) {
                ObjectNode msgData = (ObjectNode) dataNode;
                for (Map.Entry<String, String> entry : fieldsMapping.entrySet()) {
                    String nameKey = entry.getKey();
                    String[] strings = nameKey.split("\\.");
                    if(strings.length>1){
                        ObjectNode keyData = msgData;
                        for(int i = 0; i < strings.length; i++){
                            if (keyData.has(strings[i]) && null != keyData){
                                if(i == strings.length - 1){
                                    if(null != keyData){
                                        msgChanged = true;
                                        msgData.set(entry.getValue(), keyData.get(strings[i]));
                                    }
                                }else {
                                    if (dataNode.isObject()) {
                                        keyData = (ObjectNode) keyData.get(strings[i]);
                                    }
                                }
                            }
                        }
                    }
                }
                data = JacksonUtil.toString(msgData);
            }
        if (msgChanged) {
            ctx.tellSuccess(TbMsg.transformMsg(msg, msg.getType(), msg.getOriginator(), metaData, data));
        } else {
            ctx.tellSuccess(msg);
        }
    }
}
