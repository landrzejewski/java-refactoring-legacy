namespace Training.Workshop.M6.S09Observer.Step2;

/// <summary>Krok 2: kontrakt obserwatora - subject zna tylko ten interfejs. Wywołanie synchroniczne.</summary>
public interface IPaymentListener
{
    void OnPaid(ReservationPaid paidEvent);
}
