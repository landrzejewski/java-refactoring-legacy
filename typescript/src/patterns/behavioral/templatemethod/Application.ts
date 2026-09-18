import { Processor } from './Processor.js';

// Java: static nested class Application.UpperCaseProcessor
export class UpperCaseProcessor extends Processor<readonly string[], string> {
  protected read(): readonly string[] {
    return Object.freeze(['template', 'method']);
  }

  protected process(data: readonly string[]): string {
    return data.join(' ').toUpperCase();
  }

  protected write(data: string): void {
    console.log(data);
  }
}

export function run(): void {
  new UpperCaseProcessor().run();
}
