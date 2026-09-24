using Training.Workshop.Shared;

namespace Training.Workshop.M6.S17Singleton.Step3;

/// <summary>Krok 3: Extract Interface - klient zależy od kontraktu cennika, nie od singletona.</summary>
public interface ITariff
{
    Money BasePrice(string format);
}
