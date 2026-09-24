namespace Training.Workshop.M6.S09Observer.Step2;

/// <summary>Krok 2: obserwator - 1 punkt za każde pełne 10.00.</summary>
public sealed record LoyaltyPoints(ILoyaltyProgram Loyalty) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Loyalty.AddPoints(paidEvent.Email, (int)paidEvent.Amount.Amount / 10);
    }
}
