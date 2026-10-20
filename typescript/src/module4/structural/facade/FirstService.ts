import { JulLogger } from '../../JulLogger.js';

export class FirstService {
  private static readonly log = JulLogger.getLogger('pl.training.module4.structural.facade.FirstService');

  run(): void {
    FirstService.log.info('Step 1', 'run');
  }
}
