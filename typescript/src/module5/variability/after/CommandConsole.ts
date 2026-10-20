import { IllegalStateError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { Cluster } from './Cluster.js';
import type { OperatorCommand } from './OperatorCommand.js';

/**
 * Invoker: kolejkuje, wykonuje, loguje i cofa polecenia, nie znając ich treści.
 */
export class CommandConsole {
  private readonly clusterState = new Cluster();
  // Deque jako kolejka (addLast/removeFirst) i jako stos (push/pop).
  private readonly queue: OperatorCommand[] = [];
  private readonly history: OperatorCommand[] = [];
  private readonly entries: string[] = [];

  submit(command: OperatorCommand): void {
    this.queue.push(requireNonNull(command, 'command'));
  }

  runQueued(): void {
    let command = this.queue.shift();
    while (command !== undefined) {
      command.execute(this.clusterState);
      this.history.push(command);
      this.entries.push(command.name() + ' -> ' + this.clusterState.toString());
      command = this.queue.shift();
    }
  }

  undoLast(): void {
    const command = this.history.pop();
    if (command === undefined) {
      throw new IllegalStateError('nothing to undo');
    }
    command.undo(this.clusterState);
    this.entries.push('undo ' + command.name() + ' -> ' + this.clusterState.toString());
  }

  log(): readonly string[] {
    return Object.freeze([...this.entries]);
  }

  cluster(): Cluster {
    return this.clusterState;
  }
}
