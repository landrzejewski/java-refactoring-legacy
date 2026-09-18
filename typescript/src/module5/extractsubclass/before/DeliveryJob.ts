import { requireNonNull } from '../../../shared/requireNonNull.js';
import { instantToString } from '../InstantFormat.js';

// Przed Extract Subclass: pole opcjonalne używane tylko przez część obiektów (zadania zaplanowane).
export class DeliveryJob {
  private static readonly SENT = 'SENT';
  private static readonly WAITING_UNTIL = 'WAITING_UNTIL ';

  private constructor(private readonly scheduledAt: Date | undefined) {}

  static immediate(): DeliveryJob {
    return new DeliveryJob(undefined);
  }

  static scheduled(scheduledAt: Date): DeliveryJob {
    return new DeliveryJob(requireNonNull(scheduledAt, 'scheduledAt must not be null'));
  }

  dispatchAt(now: Date): string {
    requireNonNull(now, 'now must not be null');

    return this.scheduledAt !== undefined && now.getTime() < this.scheduledAt.getTime()
      ? DeliveryJob.WAITING_UNTIL + instantToString(this.scheduledAt)
      : DeliveryJob.SENT;
  }
}
