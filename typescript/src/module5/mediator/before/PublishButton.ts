import type { ApproverPicker } from './ApproverPicker.js';
import type { StatusLabel } from './StatusLabel.js';
import type { VersionField } from './VersionField.js';

// W Javie connect() i setEnabled() są package-private (@internal).
export class PublishButton {
  private isEnabled = false;
  private versionField!: VersionField;
  private picker!: ApproverPicker;
  private label!: StatusLabel;

  /** @internal */
  connect(versionField: VersionField, picker: ApproverPicker, label: StatusLabel): void {
    this.versionField = versionField;
    this.picker = picker;
    this.label = label;
  }

  click(): void {
    if (!this.isEnabled) {
      return;
    }
    this.label.setText('published ' + this.versionField.text());
    this.versionField.lock();
    this.picker.setEnabled(false);
    this.isEnabled = false;
  }

  enabled(): boolean {
    return this.isEnabled;
  }

  /** @internal */
  setEnabled(enabled: boolean): void {
    this.isEnabled = enabled;
  }
}
