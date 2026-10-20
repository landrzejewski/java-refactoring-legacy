using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Step3;

/// <summary>Krok 3: port domeny - terminal płatniczy widziany oczami kina: Money na wejściu, obiekt graniczny na wyjściu.</summary>
public interface IPaymentTerminal
{
    PaymentConfirmation Authorize(string bookingId, Money amount);
}
