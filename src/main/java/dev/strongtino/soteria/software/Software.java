package dev.strongtino.soteria.software;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Software {

    @SerializedName("_id")
    private final String name;
}
