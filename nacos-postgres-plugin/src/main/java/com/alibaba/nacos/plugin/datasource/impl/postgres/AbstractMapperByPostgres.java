package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.enums.postgres.TrustedPostgresFunctionEnum;
import com.alibaba.nacos.plugin.datasource.mapper.AbstractMapper;

/**
 * @author fuhouyu
 */
public abstract class AbstractMapperByPostgres extends AbstractMapper {

    @Override
    public String getFunction(String functionName) {
        return TrustedPostgresFunctionEnum.getFunctionByName(functionName);
    }
}
