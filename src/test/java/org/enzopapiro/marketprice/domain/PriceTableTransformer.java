package org.enzopapiro.marketprice.domain;

import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.TableTransformer;

import java.util.List;
import java.util.Map;

public class PriceTableTransformer implements TableTransformer<Price> {
    @Override
    public Price transform(DataTable dataTable) throws Throwable {

        Price price = new Price();

        List<Map<String, String>> data = dataTable.asMaps();

        final Map<String, String> valueMap = data.get(0);
        price.setId(Long.parseLong(valueMap.get("id")));
        price.setSymbol(new Symbol(valueMap.get("symbol")));
        price.setBid(Long.parseLong(valueMap.get("bid")),Integer.parseInt(valueMap.get("scale")));
        price.setAsk(Long.parseLong(valueMap.get("ask")),Integer.parseInt(valueMap.get("scale")));
        price.setTimestamp(Long.parseLong(valueMap.get("ts")));

        return price;
    }
}
