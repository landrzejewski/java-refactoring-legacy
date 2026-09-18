import { NotificationFormatter as AfterNotificationFormatter } from './collapse/after/NotificationFormatter.js';
import { NotificationFormatter as BeforeNotificationFormatter } from './collapse/before/NotificationFormatter.js';
import { RecipientList as ComposedRecipientList } from './composition/after/RecipientList.js';
import { RecipientList as InheritedRecipientList } from './composition/before/RecipientList.js';
import { DeliveryJob as AfterDeliveryJob } from './extractsubclass/after/DeliveryJob.js';
import { DeliveryJob as BeforeDeliveryJob } from './extractsubclass/before/DeliveryJob.js';
import { SmsNotification as LegacySmsNotification } from './stage0/SmsNotification.js';
import { EmailNotification } from './stage3/EmailNotification.js';
import { NotificationBatch } from './stage3/NotificationBatch.js';
import type { OutboundNotification } from './stage3/OutboundNotification.js';
import { SmsNotification } from './stage3/SmsNotification.js';

export class Module5Examples {
  private constructor() {}

  static main(_args: readonly string[] = []): void {
    const legacyNotification = new LegacySmsNotification('msg-1', 'ops', 'Deployment ready', true)
      .dispatch(true);
    const refactoredNotification = new SmsNotification('msg-1', 'ops', 'Deployment ready', true)
      .dispatch(true);

    const scheduledAt = new Date('2030-06-15T10:15:30Z');
    const now = new Date(scheduledAt.getTime() - 1000);
    const jobBefore = BeforeDeliveryJob.scheduled(scheduledAt).dispatchAt(now);
    const jobAfter = AfterDeliveryJob.scheduled(scheduledAt).dispatchAt(now);

    const formattedBefore = new BeforeNotificationFormatter()
      .format('ops@example.com', 'Deployment ready');
    const formattedAfter = new AfterNotificationFormatter()
      .format('ops@example.com', 'Deployment ready');

    const inheritedRecipients = new InheritedRecipientList();
    const composedRecipients = new ComposedRecipientList();
    inheritedRecipients.push('ops@example.com');
    composedRecipients.add('ops@example.com');

    const batch: readonly OutboundNotification[] = [
      new EmailNotification('mail-1', 'ops', 'Ready'),
      new SmsNotification('sms-1', 'ops', 'Ready', true),
    ];

    console.log('Hierarchy stages equivalent: ' + (legacyNotification === refactoredNotification));
    console.log('Extract subclass equivalent: ' + (jobBefore === jobAfter));
    console.log('Collapse hierarchy equivalent: ' + (formattedBefore === formattedAfter));
    console.log('Composition client behavior equivalent: '
      + sameElements(inheritedRecipients.snapshot(), composedRecipients.snapshot()));
    console.log('Batch results: '
      + javaListToString(new NotificationBatch().dispatchAll(batch, true)));
  }
}

function sameElements(left: readonly string[], right: readonly string[]): boolean {
  return left.length === right.length && left.every((value, index) => value === right[index]);
}

// Odpowiednik List.toString() z Javy: [a, b].
function javaListToString(values: readonly string[]): string {
  return '[' + values.join(', ') + ']';
}
