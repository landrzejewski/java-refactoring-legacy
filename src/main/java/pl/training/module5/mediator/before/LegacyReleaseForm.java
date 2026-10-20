package pl.training.module5.mediator.before;

import pl.training.module5.mediator.FormState;

/**
 * Formularz, w którym komponenty są połączone każdy z każdym (siatka zależności).
 */
public final class LegacyReleaseForm {
    private final VersionField versionField = new VersionField();
    private final ApproverPicker approverPicker = new ApproverPicker();
    private final PublishButton publishButton = new PublishButton();
    private final StatusLabel statusLabel = new StatusLabel();

    public LegacyReleaseForm() {
        versionField.connect(approverPicker, publishButton, statusLabel);
        approverPicker.connect(versionField, publishButton, statusLabel);
        publishButton.connect(versionField, approverPicker, statusLabel);
    }

    public VersionField versionField() {
        return versionField;
    }

    public ApproverPicker approverPicker() {
        return approverPicker;
    }

    public PublishButton publishButton() {
        return publishButton;
    }

    public FormState snapshot() {
        return new FormState(
                versionField.text(),
                versionField.editable(),
                approverPicker.enabled(),
                approverPicker.selection(),
                publishButton.enabled(),
                statusLabel.text());
    }
}
