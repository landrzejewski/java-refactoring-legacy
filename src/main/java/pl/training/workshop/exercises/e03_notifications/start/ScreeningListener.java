package pl.training.workshop.exercises.e03_notifications.start;

public interface ScreeningListener {

    void onMoved(MovedEvent event);

    void onCanceled(CancelledEvent event);

}
