package pl.training.workshop.exercises.e03_notifications.start;

public interface ScreeningListener {

    void onMoved(ScreeningMoved event);

    void onCanceled(ScreeningCancelled event);

}
