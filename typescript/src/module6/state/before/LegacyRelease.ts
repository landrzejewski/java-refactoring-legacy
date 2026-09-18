import { IllegalStateError } from '../../../shared/errors.js';

// Odpowiednik zagnieżdżonego enuma LegacyRelease.Status.
export enum Status {
  DRAFT = 'DRAFT',
  APPROVED = 'APPROVED',
  DEPLOYED = 'DEPLOYED',
  CANCELLED = 'CANCELLED',
}

export class LegacyRelease {
  private currentStatus: Status = Status.DRAFT;

  status(): Status {
    return this.currentStatus;
  }

  approve(): void {
    if (this.currentStatus === Status.DRAFT) {
      this.currentStatus = Status.APPROVED;
      return;
    }
    throw this.invalid('approve');
  }

  deploy(): void {
    if (this.currentStatus === Status.APPROVED) {
      this.currentStatus = Status.DEPLOYED;
      return;
    }
    throw this.invalid('deploy');
  }

  cancel(): void {
    if (this.currentStatus === Status.DRAFT || this.currentStatus === Status.APPROVED) {
      this.currentStatus = Status.CANCELLED;
      return;
    }
    throw this.invalid('cancel');
  }

  private invalid(action: string): IllegalStateError {
    return new IllegalStateError('cannot ' + action + ' release in state ' + this.currentStatus);
  }
}
