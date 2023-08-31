package dev.strongtino.soteria.license;

import com.google.gson.annotations.SerializedName;
import dev.strongtino.soteria.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class License {

    @SerializedName("_id")
    private final String key;

    private final String user;
    private final String software;

    private final long createdAt;

    private long revokedAt;
    private long duration;

    private boolean active;

    public License(String key, String user, String product, long duration) {
        this(key, user, product, System.currentTimeMillis(), 0L, duration, true);
    }

    public boolean isPermanent() {
        return duration == Long.MAX_VALUE;
    }

    public boolean isExpired() {
        return !isPermanent() && System.currentTimeMillis() > createdAt + duration;
    }

    public String getExpirationTime() {
        return isPermanent() ? "Never" : TimeUtil.formatDuration(createdAt + duration - System.currentTimeMillis());
    }
}
