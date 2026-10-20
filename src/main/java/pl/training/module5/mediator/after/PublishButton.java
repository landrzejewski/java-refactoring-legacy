package pl.training.module5.mediator.after;

import java.util.Objects;

public final class PublishButton {
    private final FormMediator mediator;
    private boolean enabled;

    PublishButton(FormMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    public void click() {
        if (!enabled) {
            return;
        }
        mediator.publishClicked();
    }

    public boolean enabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
