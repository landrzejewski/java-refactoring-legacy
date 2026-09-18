/** Java: Consumer<T>. */
type Consumer<T> = (value: T) => void;

// Java: static nested class FunctionalStyleObserver.EventSource
export class EventSource {
  private readonly observers: Consumer<string>[] = [];

  subscribe(observer: Consumer<string>): void {
    this.observers.push(observer);
  }

  publish(event: string): void {
    this.observers.forEach((o) => o(event));
  }
}

export function run(): void {
  const source = new EventSource();
  source.subscribe((msg) => console.log(`Observer 1 received: ${msg}`));
  source.subscribe((msg) => console.log(`Observer 2 received: ${msg.toUpperCase()}`));
  source.publish('Functional Observer pattern with lambdas!');
}
