package pl.training.module5.mediator.before;

/**
 * Wybór zatwierdzającego - sprawdza pole wersji i powtarza logikę etykiety.
 */
public final class ApproverPicker {
    private String selection = "";
    private boolean enabled;
    private VersionField versionField;
    private PublishButton button;
    private StatusLabel label;

    void connect(VersionField versionField, PublishButton button, StatusLabel label) {
        this.versionField = versionField;
        this.button = button;
        this.label = label;
    }

    public void choose(String approver) {
        if (!enabled) {
            return;
        }
        selection = approver;
        button.setEnabled(versionField.isValid() && hasSelection());
        if (!versionField.isValid()) {
            label.setText("enter version");
        } else if (!hasSelection()) {
            label.setText("choose approver");
        } else {
            label.setText("ready to publish");
        }
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
