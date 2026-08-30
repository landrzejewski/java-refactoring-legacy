package pl.training.module5.stage3;

@FunctionalInterface
public interface OutboundNotification {
    String dispatch(boolean successful);
}
