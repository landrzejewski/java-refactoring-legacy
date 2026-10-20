import type { Command } from './Command.js';

export class Invoker {
  private readonly commands: Command[] = []; // Java: Queue<Command> (LinkedList)

  register(command: Command): void {
    this.commands.push(command);
  }

  invokeAll(): void {
    this.commands.forEach((command) => command.execute());
  }

  invoke(command: Command): void {
    command.execute();
  }
}
