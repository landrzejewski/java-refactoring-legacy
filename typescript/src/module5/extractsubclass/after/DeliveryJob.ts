import { requireNonNull } from '../../../shared/requireNonNull.js';
import { instantToString } from '../InstantFormat.js';

// Po Extract Subclass. Obie klasy są w jednym pliku, bo w ESM cykl
// DeliveryJob -> ScheduledDeliveryJob -> DeliveryJob (extends) kończy się ReferenceError;
// ScheduledDeliveryJob.ts re-eksportuje podklasę, zachowując układ plików z Javy.
export class DeliveryJob {
  // Java: konstruktor pakietowy - tworzenie tylko przez fabryki lub podklasę.
  protected constructor() {}

  static immediate(): DeliveryJob {
    return new DeliveryJob();
  }

  static scheduled(scheduledAt: Date): DeliveryJob {
    return new ScheduledDeliveryJob(scheduledAt);
  }

  dispatchAt(now: Date): string {
    requireNonNull(now, 'now must not be null');
    return 'SENT';
  }
}

export class ScheduledDeliveryJob extends DeliveryJob {
  readonly #scheduledAt: Date;

  constructor(scheduledAt: Date) {
    super();
    this.#scheduledAt = requireNonNull(scheduledAt, 'scheduledAt must not be null');
  }

  override dispatchAt(now: Date): string {
    requireNonNull(now, 'now must not be null');

    if (now.getTime() < this.#scheduledAt.getTime()) {
      return 'WAITING_UNTIL ' + instantToString(this.#scheduledAt);
    }
    return super.dispatchAt(now);
  }
}
