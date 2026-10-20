package pl.training.module5.mediator.after;

/**
 * Jedyna zależność komponentów: zgłaszają mediatorowi, że coś się w nich zmieniło.
 */
public interface FormMediator {
    void versionChanged();

    void approverChosen();

    void publishClicked();
}
