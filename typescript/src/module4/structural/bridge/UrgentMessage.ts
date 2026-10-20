import { Message } from './Message.js';
import type { MessageSender } from './MessageSender.js';

export class UrgentMessage extends Message {
  constructor(sender: MessageSender) {
    super(sender);
  }

  send(recipient: string, text: string): void {
    this.sender.send(recipient, `[URGENT] ${text.toUpperCase()}`);
  }
}
