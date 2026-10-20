package pl.training.module5.mediator.after;

import java.util.Objects;

public final class VersionField {
    private final FormMediator mediator;
    private String text = "";
    private boolean editable = true;

    VersionField(FormMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    public void type(String value) {
        if (!editable) {
            return;
        }
        text = value;
        mediator.versionChanged();
    }

    public String text() {
        return text;
    }

    public boolean editable() {
        return editable;
    }

    void lock() {
        editable = false;
    }
}
