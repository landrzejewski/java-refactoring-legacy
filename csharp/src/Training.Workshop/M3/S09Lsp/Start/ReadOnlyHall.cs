namespace Training.Workshop.M3.S09Lsp.Start;

/// <summary>
/// Start: sala archiwalna - plan miejsc zamkniętego seansu, potrzebny tylko raportom.
/// Dziedziczy po Hall, "bo też ma plan miejsc", ale <c>Reserve</c> rzuca
/// NotSupportedException. Kontrakt Hall nie przewiduje odmowy, więc podtyp
/// wzmacnia warunek wstępny (do "nigdy") - kasa dostająca Hall wybucha w runtime.
/// </summary>
public class ReadOnlyHall : Hall
{
    public ReadOnlyHall(int capacity, IReadOnlySet<int> taken)
        : base(capacity, taken)
    {
    }

    public override void Reserve(int seat)
    {
        throw new NotSupportedException("sala archiwalna - tylko do odczytu");
    }
}
