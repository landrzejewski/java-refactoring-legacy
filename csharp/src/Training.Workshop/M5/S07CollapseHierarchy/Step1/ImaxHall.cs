namespace Training.Workshop.M5.S07CollapseHierarchy.Step1;

/// <summary>Krok 1: usunięte override'y, które tylko wołały base (IDE: "Redundant overriding member").</summary>
public class ImaxHall : Hall
{
    public ImaxHall(string name, int rows, int seatsPerRow) : base(name, rows, seatsPerRow, rows - 1)
    {
    }
}
