package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.impl.postgres.constant.PostgresConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoTagMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.Collections;

/**
 * @author fuhouyu
 */
public class ConfigInfoTagMapperByPostgres extends AbstractMapperByPostgres implements ConfigInfoTagMapper {

    @Override
    public String getDataSource() {
        return PostgresConstant.POSTGRES;
    }

    @Override
    public MapperResult findAllConfigInfoTagForDumpAllFetchRows(MapperContext context) {
        String sql = " SELECT t.id,data_id,group_id,tenant_id,tag_id,app_name,content,md5,gmt_modified  FROM (  SELECT id FROM config_info_tag  ORDER BY id OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize() + " ) g, config_info_tag t  WHERE g.id = t.id  ";
        return new MapperResult(sql, Collections.emptyList());
    }
}
