namespace Training.Workshop.M5.S28Chain.Step1;

/// <summary>Krok 1: ogniwo decyzji - pusta odpowiedź (null) znaczy "to nie moja sprawa, pytaj dalej".</summary>
public interface IRefundHandler
{
    RefundDecision? Handle(RefundRequest request);
}
