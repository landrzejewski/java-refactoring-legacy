import { Message } from './Message.js';
import type { MessageSender } from './MessageSender.js';

export class TextMessage extends Message {
  constructor(sender: MessageSender) {
    super(sender);
  }

  send(recipient: string, text: string): void {
    this.sender.send(recipient, text);
  }
}
