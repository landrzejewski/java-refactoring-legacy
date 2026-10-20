/** Java: Consumer<T>. */
type Consumer<T> = (value: T) => void;

// Java: static nested class FunctionalStyleTemplate.Processor
export class Processor {
  process(step: Consumer<string>): void {
    console.log('Start processing...');
    step('data');
    console.log('Finish processing!');
  }
}

export function run(): void {
  const p = new Processor();
  // hook behavior supplied by lambda
  p.process((d) => console.log(`Custom step with: ${d}`));
}
