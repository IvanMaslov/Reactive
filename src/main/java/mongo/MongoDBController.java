package mongo;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import domain.Currency;
import domain.Product;
import domain.User;
import org.bson.Document;
import rx.Observable;

import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBController {
    private final MongoClient client;

    private final static String DATABASE_NAME = "catalog";
    private final static String USERS_COLLECTION = "users";
    private final static String PRODUCTS_COLLECTION = "products";

    public MongoDBController(final String url) {
        client = MongoClients.create(url);
    }

    public Observable<String> addUser(int id,
                                      String nickname,
                                      Currency currency) {
        final MongoCollection<Document> users = client
                .getDatabase(DATABASE_NAME)
                .getCollection(USERS_COLLECTION);
        final Document document = new Document("id", id)
                .append("nickname", nickname)
                .append("currency", currency.toString());
        return users.find(eq("id", id)).toObservable().isEmpty().flatMap(notFound -> {
            if (notFound) {
                return users.insertOne(document).map(x -> "User(nickname = " + nickname + ") added");
            } else {
                return Observable.just("User with id = " + id + " already exists");
            }
        });
    }

    public Observable<String> addProduct(int id,
                                         String name,
                                         final Map<Currency, Double> prices) {
        final MongoCollection<Document> products = client
                .getDatabase(DATABASE_NAME)
                .getCollection(PRODUCTS_COLLECTION);
        final Document document = new Document("id", id)
                .append("name", name);
        for (Currency currency : prices.keySet()) {
            document.append("price-" + currency.toString(), prices.get(currency));
        }
        return products.find(eq("id", id)).toObservable().isEmpty().flatMap(notFound -> {
            if (notFound) {
                return products.insertOne(document).map(x -> "Product(name = " + name + ") added");
            } else {
                return Observable.just("Product(id = " + id + ") already exists");
            }
        });
    }

    public Observable<String> showUser(int userId) {
        final MongoCollection<Document> users = client
                .getDatabase(DATABASE_NAME)
                .getCollection(USERS_COLLECTION);
        Observable<Document> userDoc = users.find(eq("id", userId)).toObservable();
        return userDoc.isEmpty().flatMap(notFound -> {
            if (notFound) {
                return Observable.just("User(id = " + userId + ") not found");
            } else {
                return userDoc.map(User::new).map(User::toString);
            }
        });
    }

    public Observable<String> showProduct(int productId, int userId) {
        Observable<User> user = client
                .getDatabase(DATABASE_NAME)
                .getCollection(USERS_COLLECTION)
                .find(eq("id", userId)).toObservable().map(User::new);
        return user.isEmpty().flatMap(notFoundUser -> {
            if (notFoundUser) {
                return Observable.just("User(id = " + userId + ") not found");
            } else {
                Observable<Product> product = client
                        .getDatabase(DATABASE_NAME)
                        .getCollection(PRODUCTS_COLLECTION)
                        .find(eq("id", productId)).toObservable().map(Product::new);
                return product.isEmpty().flatMap(notFoundProduct -> {
                    if (notFoundProduct) {
                        return Observable.just("Product(id = " + productId + ") not found");
                    } else {
                        return user.flatMap(u -> product.map(
                                p -> p.toStringInCurrency(u.getCurrency())));
                    }
                });
            }
        });
    }


}
