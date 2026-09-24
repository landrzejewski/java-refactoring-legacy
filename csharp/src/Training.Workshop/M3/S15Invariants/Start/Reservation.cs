namespace Training.Workshop.M3.S15Invariants.Start;

/// <summary>
/// Start: anemiczna klasa z publicznymi setterami. Model nie pilnuje żadnego inwariantu -
/// poprawność zależy od tego, czy KAŻDY, kto go tworzy, pamięta o walidacji.
/// </summary>
public class Reservation
{
    public string? Email { get; set; }

    public int Seats { get; set; }

    public decimal Total { get; set; }
}
