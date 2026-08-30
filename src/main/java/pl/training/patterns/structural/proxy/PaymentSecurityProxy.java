package pl.training.patterns.structural.proxy;

import java.util.Map;

public class PaymentSecurityProxy implements PaymentsService {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PaymentSecurityProxy.class.getName());
    private final PaymentsService paymentsService;

    @Override
    public void pay(Map<String, String> properties) {
        log.info("Checking security");
        paymentsService.pay(properties);
    }

    public PaymentSecurityProxy(final PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }
}
