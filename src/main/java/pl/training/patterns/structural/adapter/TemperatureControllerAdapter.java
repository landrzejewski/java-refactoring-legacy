package pl.training.patterns.structural.adapter;

public class TemperatureControllerAdapter implements TemperatureController {
    private final AirConditioningController controller;

    @Override
    public void temperatureUp(double value) {
        controller.changeTemperature(celsiusDeltaToFahrenheit(value));
    }

    @Override
    public void temperatureDown(double value) {
        controller.changeTemperature(-celsiusDeltaToFahrenheit(value));
    }

    private double celsiusDeltaToFahrenheit(double value) {
        return value * 1.8;
    }

    public TemperatureControllerAdapter(final AirConditioningController controller) {
        this.controller = controller;
    }
}
