/**
 * Bridge implementor: the channel that physically sends the message.
 * It evolves independently of the Message hierarchy.
 */
export interface MessageSender {
  send(recipient: string, content: string): void;
}
