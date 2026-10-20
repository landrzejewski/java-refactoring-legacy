package pl.training.module5.observer.after;

@FunctionalInterface
public interface ReleaseListener {
    void onReleasePublished(ReleasePublished event);
}
