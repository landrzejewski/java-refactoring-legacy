using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Step3;

/// <summary>Krok 3: nowy wariant ChildTicket dopisany w assembly - kontrola wyczerpania (S13SolutionTest) od razu wskazuje PriceCalculator.</summary>
public abstract record Ticket
{
    private protected Ticket(Money basePrice)
    {
        BasePrice = basePrice;
    }

    public Money BasePrice { get; }
}
