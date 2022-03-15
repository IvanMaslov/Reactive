import io.reactivex.netty.protocol.http.server.HttpServer;
import mongo.MongoDBController;
import rx.Observable;
import server.ServerController;

public class WebCatalog {
    public static void main(String[] args) {
        final MongoDBController mongo = new MongoDBController("mongodb://localhost:27017");
        HttpServer
                .newServer(8081)
                .start((req, resp) -> {
                    final Observable<String> response = new ServerController(mongo).process(req);
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }
}
