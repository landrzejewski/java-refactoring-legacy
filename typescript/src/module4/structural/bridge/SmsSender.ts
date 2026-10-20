import type { MessageSender } from './MessageSender.js';

export class SmsSender implements MessageSender {
  send(recipient: string, content: string): void {
    console.log(`SMS to ${recipient}: ${content}`);
  }
}
