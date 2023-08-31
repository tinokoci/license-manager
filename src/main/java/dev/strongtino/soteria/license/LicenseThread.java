package dev.strongtino.soteria.license;

import dev.strongtino.soteria.Soteria;

import java.util.ArrayList;
import java.util.List;

public class LicenseThread extends Thread {

    @Override
    public void run() {
        while (true) {
            List<String> toRemove = new ArrayList<>();

            Soteria.INSTANCE.getLicenseService().getLicenses()
                    .stream()
                    .filter(license -> license.isExpired() && license.isActive())
                    .forEach(license -> {
                        Soteria.INSTANCE.getLicenseService().revokeLicense(license);
                        toRemove.add(license.getKey());
                    });

            toRemove.forEach(key -> Soteria.INSTANCE.getLicenseService().removeLicenseFromMap(key));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
