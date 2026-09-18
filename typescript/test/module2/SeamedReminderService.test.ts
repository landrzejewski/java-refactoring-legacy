import { describe, expect, it } from 'vitest';
import { LocalDate } from '../../src/module2/LocalDate.js';
import { SeamedReminderService } from '../../src/module2/SeamedReminderService.js';
import { Subscription } from '../../src/module2/Subscription.js';

class TestableReminderService extends SeamedReminderService {
  private readonly messages: string[] = [];

  constructor(private readonly today: LocalDate) {
    super();
  }

  protected override currentDate(): LocalDate {
    return this.today;
  }

  protected override sendMessage(email: string, renewalDate: LocalDate): void {
    this.messages.push(email + '|' + renewalDate.toString());
  }

  sentMessages(): readonly string[] {
    return [...this.messages];
  }
}

describe('SeamedReminderServiceTest', () => {
  it('sendsReminderForRenewalExactlySevenDaysAway', () => {
    const service = new TestableReminderService(LocalDate.of(2026, 8, 30));
    const subscription = new Subscription('developer@example.com', LocalDate.of(2026, 9, 6));

    const sent = service.sendRenewalReminder(subscription);

    expect(sent).toBe(true);
    expect(service.sentMessages()).toEqual(['developer@example.com|2026-09-06']);
  });

  it('doesNotSendReminderMoreThanSevenDaysBeforeRenewal', () => {
    const service = new TestableReminderService(LocalDate.of(2026, 8, 30));
    const subscription = new Subscription('developer@example.com', LocalDate.of(2026, 9, 7));

    const sent = service.sendRenewalReminder(subscription);

    expect(sent).toBe(false);
    expect(service.sentMessages()).toEqual([]);
  });

  it('documentsCurrentBehaviorForPastRenewalDate', () => {
    const service = new TestableReminderService(LocalDate.of(2026, 8, 30));
    const subscription = new Subscription('developer@example.com', LocalDate.of(2026, 8, 29));

    const sent = service.sendRenewalReminder(subscription);

    expect(sent).toBe(true);
    expect(service.sentMessages()).toEqual(['developer@example.com|2026-08-29']);
  });
});
