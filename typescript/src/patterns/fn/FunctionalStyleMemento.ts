/** Java: Supplier<T>. */
type Supplier<T> = () => T;

// Java: static nested class FunctionalStyleMemento.Originator
export class Originator {
  private state: string | null = null;

  setState(state: string): void {
    this.state = state;
    console.log(`Set state: ${state}`);
  }

  save(): Supplier<string | null> {
    const snapshot = this.state;
    return () => snapshot; // closure capturing state
  }

  restore(memento: Supplier<string | null>): void {
    this.state = memento();
    console.log(`Restored: ${this.state}`);
  }
}

export function run(): void {
  const o = new Originator();
  o.setState('A');
  const saved = o.save();
  o.setState('B');
  o.restore(saved);
}
