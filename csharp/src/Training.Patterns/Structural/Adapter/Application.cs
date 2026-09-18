namespace Training.Patterns.Structural.Adapter;

public class Application
{
    public static void Run()
    {
        var temperatureController = new TemperatureControllerAdapter(new AirConditioningController());
        //------------------------------------------------------------
        temperatureController.TemperatureUp(10);
    }
}
