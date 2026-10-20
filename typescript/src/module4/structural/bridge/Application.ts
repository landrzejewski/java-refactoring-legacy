import { EmailSender } from './EmailSender.js';
import type { Message } from './Message.js';
import { SmsSender } from './SmsSender.js';
import { TextMessage } from './TextMessage.js';
import { UrgentMessage } from './UrgentMessage.js';

export function run(): void {
  const email = new EmailSender();
  const sms = new SmsSender();
  const messages: readonly Message[] = Object.freeze([
    new TextMessage(email),
    new TextMessage(sms),
    new UrgentMessage(email),
    new UrgentMessage(sms),
  ]);
  //------------------------------------------------
  for (const message of messages) {
    message.send('anna', 'Server restarted');
  }
}
