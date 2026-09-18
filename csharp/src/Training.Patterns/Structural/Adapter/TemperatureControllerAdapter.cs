namespace Training.Patterns.Structural.Adapter;

public class TemperatureControllerAdapter : ITemperatureController
{
    private readonly AirConditioningController controller;

    public void TemperatureUp(double value)
    {
        controller.ChangeTemperature(CelsiusDeltaToFahrenheit(value));
    }

    public void TemperatureDown(double value)
    {
        controller.ChangeTemperature(-CelsiusDeltaToFahrenheit(value));
    }

    private static double CelsiusDeltaToFahrenheit(double value) => value * 1.8;

    public TemperatureControllerAdapter(AirConditioningController controller)
    {
        this.controller = controller;
    }
}
