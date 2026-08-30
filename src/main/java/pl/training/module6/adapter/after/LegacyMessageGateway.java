package pl.training.module6.adapter.after;

@FunctionalInterface
public interface LegacyMessageGateway {
    String transmit(String destination, String body);
}
