package pl.training.module5.adapter.after;

@FunctionalInterface
public interface LegacyMessageGateway {
    String transmit(String destination, String body);
}
