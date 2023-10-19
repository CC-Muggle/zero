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
package com.solax.thingsboard.rule.engine.util;

import org.thingsboard.rule.engine.api.TbContext;
import org.thingsboard.server.common.data.EntityTypeEnum;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.id.EntityId;

import java.util.List;

public class EntitiesByNameAndTypeLoader {

    private static final List<EntityTypeEnum> AVAILABLE_ENTITY_TYPES = List.of(
            EntityTypeEnum.DEVICE,
            EntityTypeEnum.ASSET,
            EntityTypeEnum.ENTITY_VIEW,
            EntityTypeEnum.EDGE,
            EntityTypeEnum.USER);

    public static EntityId findEntityId(TbContext ctx, EntityTypeEnum entityTypeEnum, String entityName) {
        SearchTextBasedWithAdditionalInfo<? extends EntityId> targetEntity;
        switch (entityTypeEnum) {
            case DEVICE:
                targetEntity = ctx.getDeviceService().findDeviceByTenantIdAndName(ctx.getTenantId(), entityName);
                break;
            case ASSET:
                targetEntity = ctx.getAssetService().findAssetByTenantIdAndName(ctx.getTenantId(), entityName);
                break;
            case ENTITY_VIEW:
                targetEntity = ctx.getEntityViewService().findEntityViewByTenantIdAndName(ctx.getTenantId(), entityName);
                break;
            case EDGE:
                targetEntity = ctx.getEdgeService().findEdgeByTenantIdAndName(ctx.getTenantId(), entityName);
                break;
            case USER:
                targetEntity = ctx.getUserService().findUserByTenantIdAndEmail(ctx.getTenantId(), entityName);
                break;
            default:
                throw new IllegalStateException("Unexpected entity type " + entityTypeEnum.name());
        }
        if (targetEntity == null) {
            throw new IllegalStateException("Failed to found " + entityTypeEnum.name() + "  entity by name: '" + entityName + "'!");
        }
        return targetEntity.getId();
    }

    public static void checkEntityType(EntityTypeEnum entityTypeEnum) {
        if (!AVAILABLE_ENTITY_TYPES.contains(entityTypeEnum)) {
            throw new IllegalStateException("Unexpected entity type " + entityTypeEnum.name());
        }
    }

}
