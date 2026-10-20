import type { MessageSender } from './MessageSender.js';

export class EmailSender implements MessageSender {
  send(recipient: string, content: string): void {
    console.log(`Email to ${recipient}: ${content}`);
  }
}
