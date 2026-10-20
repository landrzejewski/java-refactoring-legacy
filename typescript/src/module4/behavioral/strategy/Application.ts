import { IncrementalIdGenerator } from './IncrementalIdGenerator.js';
import { Service } from './Service.js';
import { UuidGenerator } from './UuidGenerator.js';

export function run(): void {
  new Service(new UuidGenerator()).run();
  new Service(new IncrementalIdGenerator()).run();
}
