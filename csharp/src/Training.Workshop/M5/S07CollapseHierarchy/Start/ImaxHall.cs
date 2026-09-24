namespace Training.Workshop.M5.S07CollapseHierarchy.Start;

/// <summary>
/// Start: podklasa bez własnego stanu. Override'y tylko wołają base, a jedyna różnica to reguła
/// w konstruktorze (VIP w dwóch ostatnich rzędach). Nikt nie sprawdza <c>is ImaxHall</c>.
/// </summary>
public class ImaxHall : Hall
{
    public ImaxHall(string name, int rows, int seatsPerRow) : base(name, rows, seatsPerRow, rows - 1)
    {
    }

    public override int Capacity => base.Capacity;

    public override string Describe()
    {
        return base.Describe();
    }
}
