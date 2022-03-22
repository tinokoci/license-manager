package dev.strongtino.soteria.database.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import dev.strongtino.soteria.database.Credentials;
import dev.strongtino.soteria.database.DatabaseService;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoDatabaseService implements DatabaseService {

    private static final int DEFAULT_PORT = 27017;

    private MongoDatabase database;

    @Override
    public MongoDatabaseService connect(Credentials credentials) {
        MongoClient client;

        if (credentials.getUrl() != null) {
            client = new MongoClient(new MongoClientURI(credentials.getUrl()));
        } else {
            client = new MongoClient(credentials.getHost(), DEFAULT_PORT);
        }
        database = client.getDatabase(getDatabase());
        return this;
    }

    @Override
    public void insert(String collection, Document document) {
        database.getCollection(collection).insertOne(document);
    }

    @Override
    public UpdateResult update(String collection, Bson query, Document document) {
        return database.getCollection(collection).replaceOne(query, document, new ReplaceOptions().upsert(true));
    }

    @Override
    public DeleteResult delete(String collection, Bson query) {
        return database.getCollection(collection).deleteOne(query);
    }

    @Override
    public Optional<Document> findOne(String collection, Bson query) {
        return Optional.ofNullable(database.getCollection(collection).find(query).first());
    }

    @Override
    public List<Document> findAll(String collection, Bson query) {
        return database.getCollection(collection).find(query).into(new ArrayList<>());
    }

    @Override
    public List<Document> findAll(String collection) {
        return database.getCollection(collection).find().into(new ArrayList<>());
    }
}
