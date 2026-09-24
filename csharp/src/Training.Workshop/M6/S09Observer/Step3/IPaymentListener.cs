namespace Training.Workshop.M6.S09Observer.Step3;

/// <summary>Krok 3: kontrakt obserwatora - subject zna tylko ten interfejs. Wywołanie synchroniczne.</summary>
public interface IPaymentListener
{
    void OnPaid(ReservationPaid paidEvent);
}
