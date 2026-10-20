import { LocalDate } from './LocalDate.js';
import type { Subscription } from './Subscription.js';

// Klasa celowo otwarta na dziedziczenie: chronione metody są szwami (seams).
export class SeamedReminderService {
  sendRenewalReminder(subscription: Subscription): boolean {
    const today = this.currentDate();

    if (subscription.renewalDate.isAfter(today.plusDays(7))) {
      return false;
    }

    this.sendMessage(subscription.email, subscription.renewalDate);
    return true;
  }

  protected currentDate(): LocalDate {
    return LocalDate.now();
  }

  protected sendMessage(email: string, renewalDate: LocalDate): void {
    console.log(`Sent renewal reminder to ${email} for ${renewalDate.toString()}`);
  }
}
