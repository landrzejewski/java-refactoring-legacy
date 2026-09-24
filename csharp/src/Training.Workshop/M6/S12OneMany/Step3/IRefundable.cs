using Training.Workshop.Shared;

namespace Training.Workshop.M6.S12OneMany.Step3;

/// <summary>Krok 3: wspólny kontrakt "jednego" i "wielu" - kwota do zwrotu przed potrąceniem.</summary>
public interface IRefundable
{
    Money RefundableAmount(DateTime now);
}
