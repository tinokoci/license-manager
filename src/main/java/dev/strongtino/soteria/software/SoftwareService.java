package dev.strongtino.soteria.software;

import dev.strongtino.soteria.Soteria;
import dev.strongtino.soteria.util.DatabaseUtil;
import dev.strongtino.soteria.util.Task;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoftwareService {

    private final Map<String, Software> softwareMap = new ConcurrentHashMap<>();

    public SoftwareService() {
        Task.async(() -> Soteria.INSTANCE.getDatabaseService().getDocuments(DatabaseUtil.COLLECTION_SOFTWARE)
                .stream()
                .map(document -> Soteria.GSON.fromJson(document.toJson(), Software.class))
                .forEach(this::addSoftwareToMap)
        );
    }

    public Software createSoftware(String name) {
        if (softwareMap.containsKey(name.toLowerCase())) {
            return null;
        }
        Software software = new Software(name);

        Soteria.INSTANCE.getDatabaseService().insertDocument(DatabaseUtil.COLLECTION_SOFTWARE, Document.parse(Soteria.GSON.toJson(software)));
        addSoftwareToMap(software);

        return software;
    }

    public boolean deleteSoftware(String name) {
        name = name.toLowerCase();
        Software software = softwareMap.get(name);

        if (software == null) return false;

        Soteria.INSTANCE.getDatabaseService().deleteDocument(DatabaseUtil.COLLECTION_SOFTWARE, "_id", software.getName());
        softwareMap.remove(name);

        return true;
    }

    @Nullable
    public Software getSoftware(String name) {
        return softwareMap.get(name.toLowerCase());
    }

    public void addSoftwareToMap(Software software) {
        softwareMap.put(software.getName().toLowerCase(), software);
    }

    public Collection<Software> getSoftware() {
        return softwareMap.values();
    }
}
