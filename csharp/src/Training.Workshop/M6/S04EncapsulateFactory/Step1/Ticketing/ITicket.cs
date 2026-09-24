using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step1.Ticketing;

/// <summary>Krok 1: bez zmian. Wspólny kontrakt biletu - jedyny typ, który klient naprawdę potrzebuje znać.</summary>
public interface ITicket
{
    Money Price();

    string Describe();
}
