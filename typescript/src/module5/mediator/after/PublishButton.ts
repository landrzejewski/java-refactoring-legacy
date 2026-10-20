import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { FormMediator } from './FormMediator.js';

// W Javie konstruktor i setEnabled() są package-private (@internal).
export class PublishButton {
  private readonly mediator: FormMediator;
  private isEnabled = false;

  /** @internal */
  constructor(mediator: FormMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  click(): void {
    if (!this.isEnabled) {
      return;
    }
    this.mediator.publishClicked();
  }

  enabled(): boolean {
    return this.isEnabled;
  }

  /** @internal */
  setEnabled(enabled: boolean): void {
    this.isEnabled = enabled;
  }
}
