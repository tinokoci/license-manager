package dev.strongtino.soteria.license.request;

import dev.strongtino.soteria.license.LicenseController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Request {

    private final int index;
    private final String address;
    private final String key;
    private final String software;
    private final long requestedAt;
    private final LicenseController.ValidationType validationType;

    public Request(String address, String key) {
        this(0, address, key, null, System.currentTimeMillis(), null);
    }

    public boolean isRecent() {
        return System.currentTimeMillis() - requestedAt < 60_000L;
    }
}
