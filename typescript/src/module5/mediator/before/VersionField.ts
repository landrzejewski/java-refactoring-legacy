import type { ApproverPicker } from './ApproverPicker.js';
import type { PublishButton } from './PublishButton.js';
import type { StatusLabel } from './StatusLabel.js';

/**
 * Pole wersji zna trzy inne komponenty i samo wylicza ich stan.
 * W Javie connect(), isValid() i lock() są package-private (@internal).
 */
export class VersionField {
  private value = '';
  private isEditable = true;
  private picker!: ApproverPicker;
  private button!: PublishButton;
  private label!: StatusLabel;

  /** @internal */
  connect(picker: ApproverPicker, button: PublishButton, label: StatusLabel): void {
    this.picker = picker;
    this.button = button;
    this.label = label;
  }

  type(value: string): void {
    if (!this.isEditable) {
      return;
    }
    this.value = value;
    const valid = this.isValid();
    this.picker.setEnabled(valid);
    if (!valid) {
      this.picker.clear();
    }
    this.button.setEnabled(valid && this.picker.hasSelection());
    if (!valid) {
      this.label.setText('enter version');
    } else if (!this.picker.hasSelection()) {
      this.label.setText('choose approver');
    } else {
      this.label.setText('ready to publish');
    }
  }

  text(): string {
    return this.value;
  }

  editable(): boolean {
    return this.isEditable;
  }

  /** @internal */
  isValid(): boolean {
    return /^\d+\.\d+\.\d+$/.test(this.value);
  }

  /** @internal */
  lock(): void {
    this.isEditable = false;
  }
}
