import { FormState } from '../FormState.js';
import { ApproverPicker } from './ApproverPicker.js';
import { PublishButton } from './PublishButton.js';
import { StatusLabel } from './StatusLabel.js';
import { VersionField } from './VersionField.js';

/**
 * Formularz, w którym komponenty są połączone każdy z każdym (siatka zależności).
 */
export class LegacyReleaseForm {
  private readonly version = new VersionField();
  private readonly approver = new ApproverPicker();
  private readonly publish = new PublishButton();
  private readonly statusLabel = new StatusLabel();

  constructor() {
    this.version.connect(this.approver, this.publish, this.statusLabel);
    this.approver.connect(this.version, this.publish, this.statusLabel);
    this.publish.connect(this.version, this.approver, this.statusLabel);
  }

  versionField(): VersionField {
    return this.version;
  }

  approverPicker(): ApproverPicker {
    return this.approver;
  }

  publishButton(): PublishButton {
    return this.publish;
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
}
