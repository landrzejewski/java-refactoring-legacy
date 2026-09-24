using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step3;

/// <summary>Krok 3: bez zmian - wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów.</summary>
public interface IPricedTicket
{
    Money Price();

    string Description();
}
