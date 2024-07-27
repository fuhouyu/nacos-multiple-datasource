package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.plugin.datasource.impl.postgres.constant.PostgresConstant;
import com.alibaba.nacos.plugin.datasource.mapper.GroupCapacityMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/**
 * @author fuhouyu
 */
public class GroupCapacityMapperByPostgres extends AbstractMapperByPostgres implements GroupCapacityMapper {
    @Override
    public String getDataSource() {
        return PostgresConstant.POSTGRES;
    }

    @Override
    public MapperResult selectGroupInfoBySize(MapperContext context) {
        String sql = "SELECT id, group_id FROM group_capacity WHERE id > ? LIMIT ?";
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter("id"), context.getPageSize()));
    }
}
