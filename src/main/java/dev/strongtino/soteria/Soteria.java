package dev.strongtino.soteria;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import dev.strongtino.soteria.command.LicenseCommand;
import dev.strongtino.soteria.command.RequestsCommand;
import dev.strongtino.soteria.command.SoftwareCommand;
import dev.strongtino.soteria.database.DatabaseService;
import dev.strongtino.soteria.license.LicenseService;
import dev.strongtino.soteria.license.request.RequestService;
import dev.strongtino.soteria.software.SoftwareService;
import dev.strongtino.soteria.util.ConfigFile;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Getter
public enum Soteria {

    INSTANCE;

    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .create();

    private final ConfigFile config = new ConfigFile();

    private JDA jda;

    private ExecutorService executorService;
    private DatabaseService databaseService;
    private LicenseService licenseService;
    private RequestService requestService;
    private SoftwareService softwareService;

    void start() {
        connect();
        loadServices();
        loadCommands();
    }

    private void connect() {
        try {
            JDABuilder builder = JDABuilder.createDefault(config.getString("jda-token"));

            builder.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
            builder.setActivity(Activity.watching(config.getString("jda-presence")));

            jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void loadServices() {
        executorService = Executors.newCachedThreadPool();
        databaseService = new DatabaseService();
        licenseService = new LicenseService();
        requestService = new RequestService();
        softwareService = new SoftwareService();
    }

    private void loadCommands() {
        Stream.of(
                new LicenseCommand(),
                new RequestsCommand(),
                new SoftwareCommand()
        ).forEach(command -> jda.addEventListener(command));
    }
}
