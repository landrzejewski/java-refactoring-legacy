using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step3.Ticketing;

/// <summary>Krok 3: publiczny kontrakt biletu - bez zmian.</summary>
public interface ITicket
{
    Money Price();

    string Describe();
}
