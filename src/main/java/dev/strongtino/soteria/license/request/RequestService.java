package dev.strongtino.soteria.license.request;

import dev.strongtino.soteria.Soteria;
import dev.strongtino.soteria.license.LicenseController;
import dev.strongtino.soteria.software.Software;
import dev.strongtino.soteria.util.DatabaseUtil;
import dev.strongtino.soteria.util.Task;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RequestService {

    // This could be made so it can be set per license if someone has to start a lot of application
    // instances at the same time, but this is just for "demonstrable purposes"
    private static final int REQUESTS_PER_MINUTE = 10;

    private final List<Request> recentRequests = Collections.synchronizedList(new ArrayList<>());

    public RequestService() {
        new RequestThread().start();
    }

    public void insertRequest(String address, String key, String software, LicenseController.ValidationType type) {
        recentRequests.add(new Request(address, key));

        Task.async(() -> {
            Request request = new Request(getRequestsByAddress(address).size() + 1, address, key, software, System.currentTimeMillis(), type);

            Soteria.INSTANCE.getDatabaseService().insertDocument(DatabaseUtil.COLLECTION_REQUESTS, Document.parse(Soteria.GSON.toJson(request)));
        });
    }

    public List<Request> getRequestsByAddress(String address) {
        return Soteria.INSTANCE.getDatabaseService().getDocuments(DatabaseUtil.COLLECTION_REQUESTS, "address", address)
                .stream()
                .map(document -> Soteria.GSON.fromJson(document.toJson(), Request.class))
                .collect(Collectors.toList());
    }

    public List<Request> getRequestsBySoftware(Software software) {
        return Soteria.INSTANCE.getDatabaseService().getDocuments(DatabaseUtil.COLLECTION_REQUESTS, "software", software.getName())
                .stream()
                .map(document -> Soteria.GSON.fromJson(document.toJson(), Request.class))
                .collect(Collectors.toList());
    }

    public List<Request> getRequestsByKey(String key) {
        return Soteria.INSTANCE.getDatabaseService().getDocuments(DatabaseUtil.COLLECTION_REQUESTS, "key", key)
                .stream()
                .map(document -> Soteria.GSON.fromJson(document.toJson(), Request.class))
                .collect(Collectors.toList());
    }

    public boolean detectedTooManyRequests(String address) {
        return getRecentRequests().stream().filter(request -> request.getAddress().equals(address)).count() > REQUESTS_PER_MINUTE;
    }

    public List<Request> getRecentRequests(String key) {
        return recentRequests.stream().filter(request -> request.getKey().equals(key)).collect(Collectors.toList());
    }

    public List<Request> getRecentRequests() {
        return recentRequests;
    }
}
