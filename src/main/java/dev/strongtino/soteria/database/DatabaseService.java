package dev.strongtino.soteria.database;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.lang.Nullable;
import dev.strongtino.soteria.Soteria;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    private final MongoDatabase database;

    public DatabaseService() {
        MongoClient client = new MongoClient(new ServerAddress(
                Soteria.INSTANCE.getConfig().getString("mongo-address"),
                Soteria.INSTANCE.getConfig().getInteger("mongo-port")
        ));
        database = client.getDatabase(Soteria.INSTANCE.getConfig().getString("mongo-database"));
    }

    public void insertDocument(String collection, Document document) {
        database.getCollection(collection).insertOne(document);
    }

    public void updateDocument(String collection, String key, Object value, Document document) {
        database.getCollection(collection).replaceOne(Filters.eq(key, value), document, new UpdateOptions().upsert(true));
    }

    public void deleteDocument(String collection, String key, String value) {
        database.getCollection(collection).deleteOne(Filters.eq(key, value));
    }

    public List<Document> getDocuments(String collection, String key, String value) {
        return database.getCollection(collection).find(Filters.eq(key, value)).into(new ArrayList<>());
    }

    public List<Document> getDocuments(String collection) {
        return database.getCollection(collection).find().into(new ArrayList<>());
    }

    public boolean exists(String collection, String key, Object value) {
        return database.getCollection(collection).find(Filters.eq(key, value)).first() != null;
    }

    @Nullable
    public Document getDocument(String collection, String key, String value) {
        return database.getCollection(collection).find(Filters.eq(key, value)).first();
    }
}
