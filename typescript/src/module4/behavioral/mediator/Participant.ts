import type { ChatMediator } from './ChatMediator.js';

/**
 * Colleague: knows only the mediator, not the other participants.
 */
export class Participant {
  constructor(
    private readonly name: string,
    private readonly mediator: ChatMediator,
  ) {
    mediator.join(this);
  }

  getName(): string {
    return this.name;
  }

  send(text: string): void {
    console.log(`${this.name} sends: ${text}`);
    this.mediator.broadcast(this, text);
  }

  receive(from: string, text: string): void {
    console.log(`${this.name} received from ${from}: ${text}`);
  }
}
