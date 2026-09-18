namespace Training.Patterns.Structural.Adapter;

public class AirConditioningController
{
    private static readonly JulLogger log = JulLogger.GetLogger(typeof(AirConditioningController));

    // virtual: Java methods are overridable by default (the tests record calls through a subclass)
    public virtual void ChangeTemperature(double deltaInFahrenheit)
    {
        log.Info("Changing temperature: " + JavaText.Of(deltaInFahrenheit));
    }
}
