package server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Currency;
import rx.Observable;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import mongo.MongoDBController;

public class ServerController {
    private final MongoDBController mongo;

    public ServerController(MongoDBController mongo) {
        this.mongo = mongo;
    }

    public Observable<String> process(HttpServerRequest<ByteBuf> request) {
        try {
            final String path = request.getDecodedPath().substring(1);
            switch (path) {
                case "add_user":
                    return addUser(request);
                case "add_product":
                    return addProduct(request);
                case "show_user":
                    return showUser(request);
                case "show_product":
                    return showProduct(request);
                default:
                    throw new RuntimeException("Bad request");
            }
        } catch (final Exception e) {
            return Observable.just("Error while parsing request: " + e.getMessage());
        }
    }

    private Observable<String> addUser(HttpServerRequest<ByteBuf> request) {
        final int id = Integer.parseInt(request.getQueryParameters().get("id").get(0));
        final String name = request.getQueryParameters().get("name").get(0);
        final Currency currency = Currency.getFromString(request.getQueryParameters().get("currency").get(0));
        return mongo.addUser(id, name, currency);
    }

    private Observable<String> addProduct(HttpServerRequest<ByteBuf> request) {
        final int id = Integer.parseInt(request.getQueryParameters().get("id").get(0));
        final String name = request.getQueryParameters().get("name").get(0);
        final Map<Currency, Double> prices = new HashMap<>();
        for (Currency currency : Currency.values()) {
            final List<String> cur = request.getQueryParameters().get(currency.toString());
            prices.put(currency, Double.parseDouble(cur.get(0)));
        }
        return mongo.addProduct(id, name, prices);
    }

    private Observable<String> showUser(HttpServerRequest<ByteBuf> request) {
        final int userId = Integer.parseInt(request.getQueryParameters().get("id").get(0));
        return mongo.showUser(userId);
    }

    private Observable<String> showProduct(HttpServerRequest<ByteBuf> request) {
        final int productId = Integer.parseInt(request.getQueryParameters().get("productId").get(0));
        final int userId = Integer.parseInt(request.getQueryParameters().get("userId").get(0));
        return mongo.showProduct(productId, userId);
    }

}
