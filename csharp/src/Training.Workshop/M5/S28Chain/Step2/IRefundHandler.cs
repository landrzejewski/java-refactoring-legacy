namespace Training.Workshop.M5.S28Chain.Step2;

/// <summary>Krok 2: bez zmian - ogniwo decyzji - pusta odpowiedź (null) znaczy "to nie moja sprawa, pytaj dalej".</summary>
public interface IRefundHandler
{
    RefundDecision? Handle(RefundRequest request);
}
