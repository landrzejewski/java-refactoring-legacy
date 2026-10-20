import type { FirstService } from './FirstService.js';
import type { SecondService } from './SecondService.js';

export class Facade {
  constructor(
    private readonly firstService: FirstService,
    private readonly secondService: SecondService,
  ) {}

  run(): void {
    this.firstService.run();
    this.secondService.run();
  }
}
