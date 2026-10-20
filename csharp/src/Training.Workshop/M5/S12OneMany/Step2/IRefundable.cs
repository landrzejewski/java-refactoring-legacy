using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12OneMany.Step2;

/// <summary>Krok 2: wspólny kontrakt "jednego" i "wielu" - kwota do zwrotu przed potrąceniem.</summary>
public interface IRefundable
{
    Money RefundableAmount(DateTime now);
}
