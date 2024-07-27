package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.impl.postgres.constant.PostgresConstant;
import com.alibaba.nacos.plugin.datasource.mapper.TenantInfoMapper;

/**
 * @author fuhouyu
 */
public class TenantInfoMapperByPostgres extends AbstractMapperByPostgres implements TenantInfoMapper {
    @Override
    public String getDataSource() {
        return PostgresConstant.POSTGRES;
    }
}
