import type { AirConditioningController } from './AirConditioningController.js';
import type { TemperatureController } from './TemperatureController.js';

export class TemperatureControllerAdapter implements TemperatureController {
  constructor(private readonly controller: AirConditioningController) {}

  temperatureUp(value: number): void {
    this.controller.changeTemperature(this.celsiusDeltaToFahrenheit(value));
  }

  temperatureDown(value: number): void {
    this.controller.changeTemperature(-this.celsiusDeltaToFahrenheit(value));
  }

  private celsiusDeltaToFahrenheit(value: number): number {
    return value * 1.8;
  }
}
