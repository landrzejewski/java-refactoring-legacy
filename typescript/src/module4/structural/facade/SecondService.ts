import { JulLogger } from '../../JulLogger.js';

export class SecondService {
  private static readonly log = JulLogger.getLogger('pl.training.module4.structural.facade.SecondService');

  run(): void {
    SecondService.log.info('Step 2', 'run');
  }
}
