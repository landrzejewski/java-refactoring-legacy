using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step1;

/// <summary>Krok 1: kontrakt strategii - wysokość zniżki dla ceny bazowej i typu biletu.</summary>
public interface IDiscountPolicy
{
    Money Discount(Money basePrice, string ticketType);
}
