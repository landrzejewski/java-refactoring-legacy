using Training.Workshop.Shared;

namespace Training.Workshop.M8.S01BranchByAbstraction.Step4;

/// <summary>
/// Krok 4: abstrakcja zostaje jako szew dla testów i kolejnych zmian.
/// Stara implementacja i przełącznik zostały usunięte (Safe Delete).
/// </summary>
public interface ITicketPricing
{
    Money Total(BookingRequest request);
}
