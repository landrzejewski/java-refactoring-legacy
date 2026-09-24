namespace Training.Workshop.M7.S06ParameterObject.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): planner nie waliduje - poprawność gwarantuje ScreeningSlot.
/// Stare sygnatury nadal delegują, ale teraz rzucają już przy budowie ScreeningSlot.
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
        return slot.Format switch
        {
            "2D" => 25.00m,
            "3D" => 32.00m,
            _ => 40.00m,
        };
    }
}
