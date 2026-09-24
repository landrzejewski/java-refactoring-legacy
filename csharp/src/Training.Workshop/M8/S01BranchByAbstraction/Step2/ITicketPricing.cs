using Training.Workshop.Shared;

namespace Training.Workshop.M8.S01BranchByAbstraction.Step2;

/// <summary>
/// Krok 2: abstrakcja - kontrakt projektowany pod przyszłą implementację (Money, nie double).
/// To jest "gałąź" w Branch by Abstraction: w kodzie, nie w systemie kontroli wersji.
/// </summary>
public interface ITicketPricing
{
    Money Total(BookingRequest request);
}
