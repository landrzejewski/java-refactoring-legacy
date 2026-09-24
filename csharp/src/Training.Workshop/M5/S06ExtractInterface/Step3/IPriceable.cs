using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): domyślna metoda interfejsu <c>VatAmount()</c> dodana do opublikowanego interfejsu
/// bez łamania implementacji. Używa wyłącznie operacji kontraktu, więc jest poprawna dla każdej z nich.
/// (Nowy członek ABSTRAKCYJNY złamałby każdą istniejącą implementację - źródłowo i binarnie.)
/// </summary>
public interface IPriceable
{
    Money Price { get; }

    int VatPercent { get; }

    /// <summary>Kwota VAT zawarta w cenie brutto: brutto * stawka / (100 + stawka).</summary>
    Money VatAmount()
    {
        var rate = (decimal)VatPercent;
        return new Money(Math.Round(Price.Amount * rate / (rate + 100), 2, MidpointRounding.AwayFromZero));
    }
}
