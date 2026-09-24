namespace Training.Workshop.M5.S07CollapseHierarchy.Step2;

/// <summary>
/// Krok 2: już nieużywana (klienci tworzą sale przez Hall.Imax). W bibliotece zostałaby tu jako
/// <c>[Obsolete]</c> typ zgodności na jedno wydanie.
/// </summary>
public class ImaxHall : Hall
{
    public ImaxHall(string name, int rows, int seatsPerRow) : base(name, rows, seatsPerRow, rows - 1)
    {
    }
}
