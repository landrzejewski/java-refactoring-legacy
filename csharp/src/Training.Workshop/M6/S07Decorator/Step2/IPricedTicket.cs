using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step2;

/// <summary>Krok 2: bez zmian - wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów.</summary>
public interface IPricedTicket
{
    Money Price();

    string Description();
}
