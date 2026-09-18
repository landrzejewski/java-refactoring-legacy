namespace Training.Patterns.Structural.Facade;

public class Facade
{
    private readonly FirstService firstService;
    private readonly SecondService secondService;

    public void Run()
    {
        firstService.Run();
        secondService.Run();
    }

    public Facade(FirstService firstService, SecondService secondService)
    {
        this.firstService = firstService;
        this.secondService = secondService;
    }
}
