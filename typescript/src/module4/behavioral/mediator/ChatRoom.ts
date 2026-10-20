import type { ChatMediator } from './ChatMediator.js';
import type { Participant } from './Participant.js';

export class ChatRoom implements ChatMediator {
  private readonly participants: Participant[] = [];

  join(participant: Participant): void {
    this.participants.push(participant);
  }

  broadcast(sender: Participant, text: string): void {
    for (const participant of this.participants) {
      if (participant !== sender) {
        participant.receive(sender.getName(), text);
      }
    }
  }
}
