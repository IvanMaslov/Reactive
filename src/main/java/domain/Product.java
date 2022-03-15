package domain;

import java.util.HashMap;
import java.util.Map;
import org.bson.Document;

public class Product {
    private final int id;
    private final String name;
    private final Map<Currency, Double> prices;

    public Product(int id, String name, final Map<Currency, Double> prices) {
        this.id = id;
        this.name = name;
        this.prices = prices;
    }

    private static Map<Currency, Double> parsePrices(Document document) {
        final Map<Currency, Double> res = new HashMap<>();
        for (Currency currency : Currency.values()) {
            res.put(currency, document.getDouble("price-" + currency.toString()));
        }
        return res;
    }

    public Product(Document document) {
        this(document.getInteger("id"),
                document.getString("name"),
                parsePrices(document));
    }

    public String toStringInCurrency(Currency currency) {
        return "ProductId = " + id
                + " | ProductName = " + name
                + " | ProductPrice = " + prices.get(currency)
                + System.lineSeparator();
    }
}
