package pl.training.module5.mediator.after;

import pl.training.module5.mediator.FormState;

/**
 * Mediator formularza wydania: tworzy komponenty i trzyma w jednym miejscu wszystkie
 * reguły ich współpracy. Komponenty znają tylko {@link FormMediator}, nie siebie nawzajem
 * (pasywna {@link StatusLabel} nie zna nikogo).
 */
public final class ReleaseFormMediator implements FormMediator {
    private final VersionField versionField = new VersionField(this);
    private final ApproverPicker approverPicker = new ApproverPicker(this);
    private final PublishButton publishButton = new PublishButton(this);
    private final StatusLabel statusLabel = new StatusLabel();

    public VersionField versionField() {
        return versionField;
    }

    public ApproverPicker approverPicker() {
        return approverPicker;
    }

    public PublishButton publishButton() {
        return publishButton;
    }

    @Override
    public void versionChanged() {
        boolean valid = versionValid();
        approverPicker.setEnabled(valid);
        if (!valid) {
            approverPicker.clear();
        }
        refresh();
    }

    @Override
    public void approverChosen() {
        refresh();
    }

    @Override
    public void publishClicked() {
        statusLabel.setText("published " + versionField.text());
        versionField.lock();
        approverPicker.setEnabled(false);
        publishButton.setEnabled(false);
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

    private void refresh() {
        boolean valid = versionValid();
        publishButton.setEnabled(valid && approverPicker.hasSelection());
        if (!valid) {
            statusLabel.setText("enter version");
        } else if (!approverPicker.hasSelection()) {
            statusLabel.setText("choose approver");
        } else {
            statusLabel.setText("ready to publish");
        }
    }

    private boolean versionValid() {
        return versionField.text().matches("\\d+\\.\\d+\\.\\d+");
    }
}
