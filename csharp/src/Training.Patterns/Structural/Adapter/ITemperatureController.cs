namespace Training.Patterns.Structural.Adapter;

public interface ITemperatureController
{
    void TemperatureUp(double deltaInCelsius);

    void TemperatureDown(double deltaInCelsius);
}
