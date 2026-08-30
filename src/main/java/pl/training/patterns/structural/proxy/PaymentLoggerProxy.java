package pl.training.patterns.structural.proxy;

import java.util.Map;

public class PaymentLoggerProxy implements PaymentsService {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(PaymentLoggerProxy.class.getName());
    private final PaymentsService paymentsService;

    @Override
    public void pay(Map<String, String> properties) {
        paymentsService.pay(properties);
        log.info("Payment completed");
    }

    public PaymentLoggerProxy(final PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }
}
