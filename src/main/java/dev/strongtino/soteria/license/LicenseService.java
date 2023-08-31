package dev.strongtino.soteria.license;

import dev.strongtino.soteria.Soteria;
import dev.strongtino.soteria.software.Software;
import dev.strongtino.soteria.util.DatabaseUtil;
import dev.strongtino.soteria.util.Task;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LicenseService {

    private final Map<String, License> licenses = new ConcurrentHashMap<>();

    private final int keyLength = 32;
    private final char[] keyCharactersArray = new char[keyLength];
    private final char[] possibleKeyCharactersArray = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public LicenseService() {
        Task.async(() -> Soteria.INSTANCE.getDatabaseService().getDocuments(DatabaseUtil.COLLECTION_LICENSES)
                .stream()
                .map(document -> Soteria.GSON.fromJson(document.toJson(), License.class))
                .filter(License::isActive)
                .forEach(this::addLicenseToMap)
        );
        new LicenseThread().start();
    }

    public License createLicense(String user, String product, long duration) {
        License license = new License(generateLicenseKey(), user, product, duration);

        Soteria.INSTANCE.getDatabaseService().insertDocument(DatabaseUtil.COLLECTION_LICENSES, Document.parse(Soteria.GSON.toJson(license)));
        addLicenseToMap(license);

        return license;
    }

    public void revokeLicense(License license) {
        license.setActive(false);
        license.setRevokedAt(System.currentTimeMillis());

        Soteria.INSTANCE.getDatabaseService().updateDocument(DatabaseUtil.COLLECTION_LICENSES, "_id", license.getKey(), Document.parse(Soteria.GSON.toJson(license)));
    }

    @Nullable
    public License getLicenseByKey(String key) {
        return licenses.get(key);
    }

    @Nullable
    public License getLicenseByKeyAndSoftware(String key, String software) {
        return getLicenses().stream()
                .filter(license -> license.getKey().equals(key) && license.getSoftware().equalsIgnoreCase(software))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public License getLicenseByUserAndSoftware(String user, String software) {
        return getLicenses().stream()
                .filter(license -> license.getUser().equalsIgnoreCase(user) && license.getSoftware().equalsIgnoreCase(software))
                .findFirst()
                .orElse(null);
    }

    public List<License> getLicensesBySoftware(Software software) {
        return getLicenses().stream().filter(license -> license.getSoftware().equalsIgnoreCase(software.getName())).collect(Collectors.toList());
    }

    public List<License> getLicensesByUser(String user) {
        return getLicenses().stream().filter(license -> license.getUser().equalsIgnoreCase(user)).collect(Collectors.toList());
    }

    public void addLicenseToMap(License license) {
        licenses.put(license.getKey(), license);
    }

    public void removeLicenseFromMap(String key) {
        licenses.remove(key);
    }

    public List<License> getActiveLicenses() {
        return getLicenses().stream().filter(License::isActive).collect(Collectors.toList());
    }

    public Collection<License> getLicenses() {
        return licenses.values();
    }

    public String generateLicenseKey() {
        for (int i = 0; i < keyLength; i++) {
            keyCharactersArray[i] = possibleKeyCharactersArray[ThreadLocalRandom.current().nextInt(possibleKeyCharactersArray.length)];
        }
        String key = new String(keyCharactersArray);

        // 40+ digit number of permutations, but better worry than be sorry ya know
        if (Soteria.INSTANCE.getDatabaseService().exists(DatabaseUtil.COLLECTION_LICENSES, "_id", key)) {
            return generateLicenseKey();
        }
        return new String(keyCharactersArray);
    }
}
