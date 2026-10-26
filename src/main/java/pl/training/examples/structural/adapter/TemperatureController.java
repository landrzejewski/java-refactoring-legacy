package pl.training.examples.structural.adapter;

public interface TemperatureController {

    void temperatureUp(double deltaInCelsius);

    void temperatureDown(double deltaInCelsius);

}
