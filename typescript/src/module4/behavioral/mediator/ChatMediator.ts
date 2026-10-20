import type { Participant } from './Participant.js';

/**
 * Mediator: the only object that knows every participant. Participants communicate
 * exclusively through it, so they hold no references to each other.
 */
export interface ChatMediator {
  join(participant: Participant): void;

  broadcast(sender: Participant, text: string): void;
}
