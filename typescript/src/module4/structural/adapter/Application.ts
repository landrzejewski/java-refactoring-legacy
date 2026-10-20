import { AirConditioningController } from './AirConditioningController.js';
import { TemperatureControllerAdapter } from './TemperatureControllerAdapter.js';

export function run(): void {
  const temperatureController = new TemperatureControllerAdapter(new AirConditioningController());
  //------------------------------------------------------------
  temperatureController.temperatureUp(10);
}
