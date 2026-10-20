import { FormState } from '../FormState.js';
import { ApproverPicker } from './ApproverPicker.js';
import type { FormMediator } from './FormMediator.js';
import { PublishButton } from './PublishButton.js';
import { StatusLabel } from './StatusLabel.js';
import { VersionField } from './VersionField.js';

const VERSION = /^\d+\.\d+\.\d+$/;

/**
 * Mediator formularza wydania: tworzy komponenty i trzyma w jednym miejscu wszystkie
 * reguły ich współpracy. Komponenty znają tylko FormMediator, nie siebie nawzajem
 * (pasywna StatusLabel nie zna nikogo).
 */
export class ReleaseFormMediator implements FormMediator {
  private readonly version = new VersionField(this);
  private readonly approver = new ApproverPicker(this);
  private readonly publish = new PublishButton(this);
  private readonly statusLabel = new StatusLabel();

  versionField(): VersionField {
    return this.version;
  }

  approverPicker(): ApproverPicker {
    return this.approver;
  }

  publishButton(): PublishButton {
    return this.publish;
  }

  versionChanged(): void {
    const valid = this.versionValid();
    this.approver.setEnabled(valid);
    if (!valid) {
      this.approver.clear();
    }
    this.refresh();
  }

  approverChosen(): void {
    this.refresh();
  }

  publishClicked(): void {
    this.statusLabel.setText('published ' + this.version.text());
    this.version.lock();
    this.approver.setEnabled(false);
    this.publish.setEnabled(false);
  }

  snapshot(): FormState {
    return new FormState(
      this.version.text(),
      this.version.editable(),
      this.approver.enabled(),
      this.approver.selection(),
      this.publish.enabled(),
      this.statusLabel.text(),
    );
  }

  private refresh(): void {
    const valid = this.versionValid();
    this.publish.setEnabled(valid && this.approver.hasSelection());
    if (!valid) {
      this.statusLabel.setText('enter version');
    } else if (!this.approver.hasSelection()) {
      this.statusLabel.setText('choose approver');
    } else {
      this.statusLabel.setText('ready to publish');
    }
  }

  private versionValid(): boolean {
    return VERSION.test(this.version.text());
  }
}
