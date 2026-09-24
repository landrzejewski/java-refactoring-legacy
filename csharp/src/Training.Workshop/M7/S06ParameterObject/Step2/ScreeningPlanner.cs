namespace Training.Workshop.M7.S06ParameterObject.Step2;

/// <summary>
/// Krok 2: Move Method - opis terminu przeniesiony do ScreeningSlot.Label(); stare sygnatury
/// nadal delegują (okres migracji). Walidacja nadal tu, bez zmian.
/// </summary>
public sealed class ScreeningPlanner
{
    /// <summary>Przestarzałe: użyj <see cref="Describe(ScreeningSlot)"/>.</summary>
    [Obsolete("użyj Describe(ScreeningSlot)")]
    public string Describe(string screeningId, DateOnly date, int hall, string format)
    {
        return Describe(new ScreeningSlot(screeningId, date, hall, format));
    }

    /// <summary>Przestarzałe: użyj <see cref="TicketPrice(ScreeningSlot)"/>.</summary>
    [Obsolete("użyj TicketPrice(ScreeningSlot)")]
    public decimal TicketPrice(string screeningId, DateOnly date, int hall, string format)
    {
        return TicketPrice(new ScreeningSlot(screeningId, date, hall, format));
    }

    public string Describe(ScreeningSlot slot)
    {
        return slot.Label();
    }

    public decimal TicketPrice(ScreeningSlot slot)
    {
        if (slot.Hall < 1 || slot.Hall > 8)
        {
            throw new ArgumentException(
                "nie ma sali " + slot.Hall + " (" + slot.ScreeningId + ")");
        }
        return slot.Format switch
        {
            "2D" => 25.00m,
            "3D" => 32.00m,
            "IMAX" => 40.00m,
            _ => throw new ArgumentException(
                "nieznany format " + slot.Format + " (" + slot.ScreeningId + ")"),
        };
    }
}
