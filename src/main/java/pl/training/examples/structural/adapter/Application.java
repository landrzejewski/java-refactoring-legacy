package pl.training.examples.structural.adapter;

public class Application {

    public static void main(String[] args) {
        var temperatureController = new TemperatureControllerAdapter(new AirConditioningController());
        //------------------------------------------------------------
        temperatureController.temperatureUp(10);
    }

}
