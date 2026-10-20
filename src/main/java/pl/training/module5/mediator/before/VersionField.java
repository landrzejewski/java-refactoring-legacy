package pl.training.module5.mediator.before;

/**
 * Pole wersji zna trzy inne komponenty i samo wylicza ich stan.
 */
public final class VersionField {
    private String text = "";
    private boolean editable = true;
    private ApproverPicker picker;
    private PublishButton button;
    private StatusLabel label;

    void connect(ApproverPicker picker, PublishButton button, StatusLabel label) {
        this.picker = picker;
        this.button = button;
        this.label = label;
    }

    public void type(String value) {
        if (!editable) {
            return;
        }
        text = value;
        boolean valid = isValid();
        picker.setEnabled(valid);
        if (!valid) {
            picker.clear();
        }
        button.setEnabled(valid && picker.hasSelection());
        if (!valid) {
            label.setText("enter version");
        } else if (!picker.hasSelection()) {
            label.setText("choose approver");
        } else {
            label.setText("ready to publish");
        }
    }

    public String text() {
        return text;
    }

    public boolean editable() {
        return editable;
    }

    boolean isValid() {
        return text.matches("\\d+\\.\\d+\\.\\d+");
    }

    void lock() {
        editable = false;
    }
}
