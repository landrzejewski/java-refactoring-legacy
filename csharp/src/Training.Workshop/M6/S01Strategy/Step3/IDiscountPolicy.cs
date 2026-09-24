using Training.Workshop.Shared;

namespace Training.Workshop.M6.S01Strategy.Step3;

/// <summary>Krok 3: kontrakt strategii - bez zmian.</summary>
public interface IDiscountPolicy
{
    Money Discount(Money basePrice, string ticketType);
}
