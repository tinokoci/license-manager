package dev.strongtino.soteria.database;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Credentials {

    private String url;
    private int port;

    @Builder.Default
    private String host = "localhost";
}