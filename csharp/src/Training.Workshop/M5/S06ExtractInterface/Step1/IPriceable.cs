using Training.Workshop.Shared;

namespace Training.Workshop.M5.S06ExtractInterface.Step1;

/// <summary>
/// Krok 1: Extract Interface z perspektywy klienta (Cart) - rola "coś, co ma cenę brutto i stawkę VAT".
/// Nie kopiujemy Title, Seat ani Name: koszyk ich nie używa.
/// </summary>
public interface IPriceable
{
    Money Price { get; }

    int VatPercent { get; }
}
