namespace Training.Workshop.M6.S09Observer.Step3;

/// <summary>Krok 3: obserwator - 1 punkt za każde pełne 10.00.</summary>
public sealed record LoyaltyPoints(ILoyaltyProgram Loyalty) : IPaymentListener
{
    public void OnPaid(ReservationPaid paidEvent)
    {
        Loyalty.AddPoints(paidEvent.Email, (int)paidEvent.Amount.Amount / 10);
    }
}
