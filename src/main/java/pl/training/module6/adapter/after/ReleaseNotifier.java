package pl.training.module6.adapter.after;

@FunctionalInterface
public interface ReleaseNotifier {
    String send(ReleaseMessage message);
}
