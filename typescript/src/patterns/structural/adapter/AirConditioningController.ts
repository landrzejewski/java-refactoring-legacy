import { javaDouble } from '../../JavaText.js';
import { JulLogger } from '../../JulLogger.js';

export class AirConditioningController {
  private static readonly log = JulLogger.getLogger('pl.training.patterns.structural.adapter.AirConditioningController');

  changeTemperature(deltaInFahrenheit: number): void {
    AirConditioningController.log.info(`Changing temperature: ${javaDouble(deltaInFahrenheit)}`, 'changeTemperature');
  }
}
