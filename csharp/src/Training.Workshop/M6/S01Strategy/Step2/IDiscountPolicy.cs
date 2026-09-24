using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step2;

/// <summary>Krok 2: kontrakt strategii - bez zmian względem kroku 1.</summary>
public interface IDiscountPolicy
{
    Money Discount(Money basePrice, string ticketType);
}
