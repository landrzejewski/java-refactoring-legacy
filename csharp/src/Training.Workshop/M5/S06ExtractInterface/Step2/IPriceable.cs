using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Step2;

/// <summary>Krok 2: bez zmian.</summary>
public interface IPriceable
{
    Money Price { get; }

    int VatPercent { get; }
}
