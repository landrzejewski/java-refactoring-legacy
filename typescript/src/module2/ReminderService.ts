import type { Clock } from './Clock.js';
import { LocalDate } from './LocalDate.js';
import type { Subscription } from './Subscription.js';
import { requireNonNull } from '../shared/requireNonNull.js';

export class ReminderService {
  private readonly clock: Clock;
  private readonly reminderGateway: ReminderGateway;

  constructor(clock: Clock, reminderGateway: ReminderGateway) {
    this.clock = requireNonNull(clock);
    this.reminderGateway = requireNonNull(reminderGateway);
  }

  sendRenewalReminder(subscription: Subscription): boolean {
    const today = LocalDate.now(this.clock);

    if (subscription.renewalDate.isAfter(today.plusDays(7))) {
      return false;
    }

    this.reminderGateway.send(subscription.email, subscription.renewalDate);
    return true;
  }
}

// W Javie: ReminderService.ReminderGateway.
export interface ReminderGateway {
  send(email: string, renewalDate: LocalDate): void;
}
