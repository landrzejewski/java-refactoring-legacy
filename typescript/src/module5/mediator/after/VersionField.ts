import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { FormMediator } from './FormMediator.js';

// W Javie konstruktor i lock() są package-private: komponenty tworzy i steruje nimi
// tylko ReleaseFormMediator. TS nie ma widoczności pakietowej (@internal).
export class VersionField {
  private readonly mediator: FormMediator;
  private value = '';
  private isEditable = true;

  /** @internal */
  constructor(mediator: FormMediator) {
    this.mediator = requireNonNull(mediator, 'mediator');
  }

  type(value: string): void {
    if (!this.isEditable) {
      return;
    }
    this.value = value;
    this.mediator.versionChanged();
  }

  text(): string {
    return this.value;
  }

  editable(): boolean {
    return this.isEditable;
  }

  /** @internal */
  lock(): void {
    this.isEditable = false;
  }
}
