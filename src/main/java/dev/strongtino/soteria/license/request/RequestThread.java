package dev.strongtino.soteria.license.request;

import dev.strongtino.soteria.Soteria;

import java.util.ArrayList;
import java.util.List;

public class RequestThread extends Thread {

    @Override
    public void run() {
        while (true) {
            List<Request> toRemove = new ArrayList<>();

            Soteria.INSTANCE.getRequestService().getRecentRequests()
                    .stream()
                    .filter(request -> !request.isRecent())
                    .forEach(toRemove::add);

            Soteria.INSTANCE.getRequestService().getRecentRequests().removeAll(toRemove);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
