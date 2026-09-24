namespace Training.Workshop.M3.S14ReversiblePattern.Step2;

/// <summary>
/// Krok 1: Strategy - wspólny kontrakt modeli rozliczeń.
/// Wynik: kwota dla dystrybutora, 2 miejsca po przecinku, nieujemna, bez efektów ubocznych.
/// </summary>
public interface ISettlementModel
{
    decimal Payout(Deal deal, int week, decimal ticketRevenue);
}
