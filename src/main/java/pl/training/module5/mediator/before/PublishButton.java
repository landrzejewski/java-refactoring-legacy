package pl.training.module5.mediator.before;

public final class PublishButton {
    private boolean enabled;
    private VersionField versionField;
    private ApproverPicker picker;
    private StatusLabel label;

    void connect(VersionField versionField, ApproverPicker picker, StatusLabel label) {
        this.versionField = versionField;
        this.picker = picker;
        this.label = label;
    }

    public void click() {
        if (!enabled) {
            return;
        }
        label.setText("published " + versionField.text());
        versionField.lock();
        picker.setEnabled(false);
        enabled = false;
    }

    public boolean enabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
