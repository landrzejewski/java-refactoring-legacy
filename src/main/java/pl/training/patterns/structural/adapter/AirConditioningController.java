package pl.training.patterns.structural.adapter;

public class AirConditioningController {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(AirConditioningController.class.getName());

    public void changeTemperature(double deltaInFahrenheit) {
        log.info("Changing temperature: " + deltaInFahrenheit);
    }
}
