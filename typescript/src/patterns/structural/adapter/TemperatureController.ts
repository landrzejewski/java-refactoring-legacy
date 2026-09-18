export interface TemperatureController {
  temperatureUp(deltaInCelsius: number): void;
  temperatureDown(deltaInCelsius: number): void;
}
