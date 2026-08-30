package pl.training.patterns.structural.facade;

public class Facade {
    private final FirstService firstService;
    private final SecondService secondService;

    public void run() {
        firstService.run();
        secondService.run();
    }

    public Facade(final FirstService firstService, final SecondService secondService) {
        this.firstService = firstService;
        this.secondService = secondService;
    }
}
