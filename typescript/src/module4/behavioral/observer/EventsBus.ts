import type { ServerEvent } from './ServerEvent.js';

/** Java: java.util.function.Consumer<T>. */
export interface Consumer<T> {
  accept(value: T): void;
}

export class EventsBus {
  // Java: synchronizedSet(new HashSet<>()) - synchronization not needed in single-threaded JavaScript
  private readonly consumers = new Set<Consumer<ServerEvent>>();

  addConsumer(consumer: Consumer<ServerEvent>): void {
    this.consumers.add(consumer);
  }

  publish(event: ServerEvent): void {
    this.consumers.forEach((consumer) => consumer.accept(event));
  }
}
