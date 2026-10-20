import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';
import type { FormMediator } from './FormMediator.js';

// W Javie konstruktor, hasSelection(), setEnabled() i clear() są package-private (@internal).
export class ApproverPicker {
  private readonly mediator: FormMediator;
  private selected = '';
  private isEnabled = false;

  /** @internal */
  constructor(mediator: FormMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  choose(approver: string): void {
    if (!this.isEnabled) {
      return;
    }
    this.selected = approver;
    this.mediator.approverChosen();
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
