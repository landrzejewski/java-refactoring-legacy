using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step2.Ticketing;

/// <summary>Krok 2: bez zmian. Wspólny kontrakt biletu - jedyny typ, który klient naprawdę potrzebuje znać.</summary>
public interface ITicket
{
    Money Price();

    string Describe();
}
