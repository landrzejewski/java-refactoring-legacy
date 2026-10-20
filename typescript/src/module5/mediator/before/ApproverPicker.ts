import { isBlank } from '../../support.js';
import type { PublishButton } from './PublishButton.js';
import type { StatusLabel } from './StatusLabel.js';
import type { VersionField } from './VersionField.js';

/**
 * Wybór zatwierdzającego - sprawdza pole wersji i powtarza logikę etykiety.
 * W Javie connect(), hasSelection(), setEnabled() i clear() są package-private (@internal).
 */
export class ApproverPicker {
  private selected = '';
  private isEnabled = false;
  private versionField!: VersionField;
  private button!: PublishButton;
  private label!: StatusLabel;

  /** @internal */
  connect(versionField: VersionField, button: PublishButton, label: StatusLabel): void {
    this.versionField = versionField;
    this.button = button;
    this.label = label;
  }

  choose(approver: string): void {
    if (!this.isEnabled) {
      return;
    }
    this.selected = approver;
    this.button.setEnabled(this.versionField.isValid() && this.hasSelection());
    if (!this.versionField.isValid()) {
      this.label.setText('enter version');
    } else if (!this.hasSelection()) {
      this.label.setText('choose approver');
    } else {
      this.label.setText('ready to publish');
    }
  }

  selection(): string {
    return this.selected;
  }

  enabled(): boolean {
    return this.isEnabled;
  }

  /** @internal */
  hasSelection(): boolean {
    return !isBlank(this.selected);
  }

  /** @internal */
  setEnabled(enabled: boolean): void {
    this.isEnabled = enabled;
  }

  /** @internal */
  clear(): void {
    this.selected = '';
  }
}
