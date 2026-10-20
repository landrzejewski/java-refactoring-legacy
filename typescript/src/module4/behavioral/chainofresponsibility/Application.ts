import { Logger } from './Logger.js';
import { Processor } from './Processor.js';
import { Validator } from './Validator.js';

export function run(): void {
  const chain = new Validator(new Logger(new Processor()));
  chain.handleRequest('Test');
}
