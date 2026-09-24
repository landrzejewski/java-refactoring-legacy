using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator.Step1;

/// <summary>Krok 1: wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów.</summary>
public interface IPricedTicket
{
    Money Price();

    string Description();
}
