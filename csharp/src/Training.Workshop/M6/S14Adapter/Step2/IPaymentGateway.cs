using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Step2;

/// <summary>Krok 2: preferowany interfejs kina - kwota jako Money, odmowa jako wynik, nie wyjątek.</summary>
public interface IPaymentGateway
{
    PaymentResult Pay(string reservationId, Money amount);
}
