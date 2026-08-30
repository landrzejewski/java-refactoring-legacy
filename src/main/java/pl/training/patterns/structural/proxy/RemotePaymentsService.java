package pl.training.patterns.structural.proxy;

import java.util.Map;

public class RemotePaymentsService implements PaymentsService {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(RemotePaymentsService.class.getName());

    @Override
    public void pay(Map<String, String> properties) {
        log.info("Payment started");
    }
}
