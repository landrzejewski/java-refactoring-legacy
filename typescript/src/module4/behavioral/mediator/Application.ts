import { ChatRoom } from './ChatRoom.js';
import { Participant } from './Participant.js';

export function run(): void {
  const room = new ChatRoom();
  const alice = new Participant('Alice', room);
  const bob = new Participant('Bob', room);
  new Participant('Carol', room);
  //------------------------------------------------
  alice.send('Hello everyone');
  bob.send('Hi Alice');
}
