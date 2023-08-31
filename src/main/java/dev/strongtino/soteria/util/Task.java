package dev.strongtino.soteria.util;

import dev.strongtino.soteria.Soteria;

public class Task {

    public static void async(Callback callback) {
        Soteria.INSTANCE.getExecutorService().execute(callback::run);
    }

    public interface Callback {
        void run();
    }
}
