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
package com.solax.thingsboard.rule.engine.filter;

import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.EmptyNodeConfiguration;
import org.thingsboard.rule.engine.api.RuleNode;
import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.rule.engine.api.TbNodeException;
import org.thingsboard.server.common.data.EntityTypeEnum;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.plugin.ComponentType;

@Slf4j
@RuleNode(
        type = ComponentType.FILTER,
        name = "entity type switch",
        configClazz = EmptyNodeConfiguration.class,
        relationTypes = {"Device", "Asset", "Alarm", "Entity View", "Tenant", "Customer", "User", "Dashboard", "Rule chain", "Rule node", "Edge"},
        nodeDescription = "Route incoming messages by Message Originator Type",
        nodeDetails = "Routes messages to chain according to the entity type ('Device', 'Asset', etc.).",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbNodeEmptyConfig")
public class TbOriginatorTypeSwitchNode extends TbAbstractTypeSwitchNode {

    @Override
    protected String getRelationType(TbContext ctx, EntityId originator) throws TbNodeException {
        String relationType;
        EntityTypeEnum originatorType = originator.getEntityType();
        switch (originatorType) {
            case TENANT:
                relationType = "Tenant";
                break;
            case CUSTOMER:
                relationType = "Customer";
                break;
            case USER:
                relationType = "User";
                break;
            case DASHBOARD:
                relationType = "Dashboard";
                break;
            case ASSET:
                relationType = "Asset";
                break;
            case DEVICE:
                relationType = "Device";
                break;
            case ENTITY_VIEW:
                relationType = "Entity View";
                break;
            case EDGE:
                relationType = "Edge";
                break;
            case RULE_CHAIN:
                relationType = "Rule chain";
                break;
            case RULE_NODE:
                relationType = "Rule node";
                break;
            case ALARM:
                relationType = "Alarm";
                break;
            default:
                throw new TbNodeException("Unsupported originator type: " + originatorType);
        }
        return relationType;
    }

}
