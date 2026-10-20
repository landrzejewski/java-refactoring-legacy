import type { MessageSender } from './MessageSender.js';

/**
 * Bridge abstraction: the kind of message. It holds a reference to the implementor
 * (MessageSender) instead of inheriting from it, so 2 message kinds and 2 channels
 * are 4 classes, not one subclass per combination.
 */
export abstract class Message {
  protected constructor(protected readonly sender: MessageSender) {}

  abstract send(recipient: string, text: string): void;
}
