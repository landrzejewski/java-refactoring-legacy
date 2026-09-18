import { Decimal } from 'decimal.js';
import { fixedClock } from './Clock.js';
import { InvoiceFormatter } from './InvoiceFormatter.js';
import { InvoiceLine } from './InvoiceLine.js';
import { LegacyInvoiceFormatter } from './LegacyInvoiceFormatter.js';
import { LocalDate } from './LocalDate.js';
import { OrderPlacementService } from './OrderPlacementService.js';
import { ReminderService } from './ReminderService.js';
import { Subscription } from './Subscription.js';

export class Module2Examples {
  private constructor() {}

  static main(_args: readonly string[] = []): void {
    Module2Examples.runBehaviorPreservingRefactoring();
    Module2Examples.runTestDoubleExample();
    Module2Examples.runExplicitSeamExample();
  }

  private static runBehaviorPreservingRefactoring(): void {
    const lines = [
      new InvoiceLine('BOOK', 2, new Decimal('19.99')),
      new InvoiceLine('PEN', 1, new Decimal('5.00')),
    ];
    const before = new LegacyInvoiceFormatter().format('Acme', lines);
    const after = new InvoiceFormatter().format('Acme', lines);

    console.log('Formatter outputs equal: ' + (before === after));
  }

  private static runTestDoubleExample(): void {
    const service = new OrderPlacementService(
      { priceFor: () => new Decimal('12.50') },
      { charge: () => 'AUTH-DEMO' },
      { save: () => 1 },
      { publish: (event) => console.log('Published event: ' + event.toString()) },
    );

    const order = service.place('BOOK', 2, 'TOKEN-DEMO');
    console.log('Placed order: ' + order.toString());
  }

  private static runExplicitSeamExample(): void {
    const clock = fixedClock('2026-08-30T10:00:00Z');
    const service = new ReminderService(
      clock,
      {
        send: (email, renewalDate) =>
          console.log(`Reminder: ${email} renews on ${renewalDate.toString()}`),
      },
    );

    service.sendRenewalReminder(new Subscription(
      'developer@example.com',
      LocalDate.of(2026, 9, 6)));
  }
}
