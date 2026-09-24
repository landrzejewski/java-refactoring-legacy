using Training.Workshop.Shared;

namespace Training.Workshop.M6.S14Adapter.Step3;

/// <summary>Krok 3: preferowany interfejs kina - kwota jako Money, odmowa jako wynik, nie wyjątek.</summary>
public interface IPaymentGateway
{
    PaymentResult Pay(string reservationId, Money amount);
}
