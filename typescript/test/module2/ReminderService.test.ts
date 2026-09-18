import { describe, expect, it } from 'vitest';
import { fixedClock } from '../../src/module2/Clock.js';
import { LocalDate } from '../../src/module2/LocalDate.js';
import { ReminderService, type ReminderGateway } from '../../src/module2/ReminderService.js';
import { Subscription } from '../../src/module2/Subscription.js';

class RecordingReminderGateway implements ReminderGateway {
  private readonly sent: string[] = [];

  send(email: string, renewalDate: LocalDate): void {
    this.sent.push(email + '|' + renewalDate.toString());
  }

  messages(): readonly string[] {
    return [...this.sent];
  }
}

describe('ReminderServiceTest', () => {
  it('usesInjectedClockAndGateway', () => {
    const clock = fixedClock('2026-08-30T10:00:00Z');
    const gateway = new RecordingReminderGateway();
    const service = new ReminderService(clock, gateway);
    const subscription = new Subscription('developer@example.com', LocalDate.of(2026, 9, 6));

    const sent = service.sendRenewalReminder(subscription);

    expect(sent).toBe(true);
    expect(gateway.messages()).toEqual(['developer@example.com|2026-09-06']);
  });

  it('doesNotSendReminderMoreThanSevenDaysBeforeRenewal', () => {
    const clock = fixedClock('2026-08-30T10:00:00Z');
    const gateway = new RecordingReminderGateway();
    const service = new ReminderService(clock, gateway);
    const subscription = new Subscription('developer@example.com', LocalDate.of(2026, 9, 7));

    const sent = service.sendRenewalReminder(subscription);

    expect(sent).toBe(false);
    expect(gateway.messages()).toEqual([]);
  });

  it('preservesCurrentBehaviorForPastRenewalDate', () => {
    const clock = fixedClock('2026-08-30T10:00:00Z');
    const gateway = new RecordingReminderGateway();
    const service = new ReminderService(clock, gateway);
    const subscription = new Subscription('developer@example.com', LocalDate.of(2026, 8, 29));

    const sent = service.sendRenewalReminder(subscription);

    expect(sent).toBe(true);
    expect(gateway.messages()).toEqual(['developer@example.com|2026-08-29']);
  });
});
