package pl.training.module5.adapter.after;

@FunctionalInterface
public interface ReleaseNotifier {
    String send(ReleaseMessage message);
}
