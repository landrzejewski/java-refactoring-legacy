import { LocalDate } from './LocalDate.js';
import type { Subscription } from './Subscription.js';

export class LegacyReminderService {
  sendRenewalReminder(subscription: Subscription): boolean {
    // Ukryta zależność od zegara systemowego i konsoli.
    const today = LocalDate.now();

    if (subscription.renewalDate.isAfter(today.plusDays(7))) {
      return false;
    }

    console.log(
      `Sent renewal reminder to ${subscription.email} for ${subscription.renewalDate.toString()}`);
    return true;
  }
}
