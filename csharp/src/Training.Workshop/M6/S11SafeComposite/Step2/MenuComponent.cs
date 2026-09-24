using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Step2;

/// <summary>
/// Krok 2: forma C# odpowiadająca sealed interface z Javy - zamknięty zestaw węzłów
/// (konstruktor private protected: podtypy tylko w tym projekcie), rekordy, brak mutacji po zbudowaniu.
/// </summary>
public abstract record MenuComponent
{
    private protected MenuComponent()
    {
    }

    public abstract string Name { get; }

    public abstract Money Price { get; }

    public abstract string Describe();
}
