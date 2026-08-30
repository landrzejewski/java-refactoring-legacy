package pl.training.module6.observer.after;

@FunctionalInterface
public interface ReleaseListener {
    void onReleasePublished(ReleasePublished event);
}
