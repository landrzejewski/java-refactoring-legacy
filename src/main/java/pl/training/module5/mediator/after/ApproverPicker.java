package pl.training.module5.mediator.after;

import java.util.Objects;

public final class ApproverPicker {
    private final FormMediator mediator;
    private String selection = "";
    private boolean enabled;

    ApproverPicker(FormMediator mediator) {
        this.mediator = Objects.requireNonNull(mediator, "mediator");
    }

    public void choose(String approver) {
        if (!enabled) {
            return;
        }
        selection = approver;
        mediator.approverChosen();
    }

    public String selection() {
        return selection;
    }

    public boolean enabled() {
        return enabled;
    }

    boolean hasSelection() {
        return !selection.isBlank();
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void clear() {
        selection = "";
    }
}
