package org.enzopapiro.marketprice.domain;

import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;

public class PriceRegistryConfigurer implements TypeRegistryConfigurer {
    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        typeRegistry.defineDataTableType(new DataTableType(Price.class,new PriceTableTransformer()));
    }
}
