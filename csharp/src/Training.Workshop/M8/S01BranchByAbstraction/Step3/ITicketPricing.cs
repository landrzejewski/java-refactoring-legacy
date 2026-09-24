using Training.Workshop.Shared;

namespace Training.Workshop.M8.S01BranchByAbstraction.Step3;

/// <summary>
/// Krok 3 (bez zmian): abstrakcja - kontrakt pod przyszłą implementację (Money, nie double).
/// To jest "gałąź" w Branch by Abstraction: w kodzie, nie w systemie kontroli wersji.
/// </summary>
public interface ITicketPricing
{
    Money Total(BookingRequest request);
}
