using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step3.Ticketing;

public static partial class Tickets
{
    /// <summary>Krok 3: prywatny typ fabryki, miejsce VIP: +10.00.</summary>
    private sealed record VipTicket(string Title, Money Base, int Row) : ITicket
    {
        public Money Price()
        {
            return Base.Plus(Money.Of("10.00"));
        }

        public string Describe()
        {
            return Title + " r" + Row + " VIP " + Price();
        }
    }
}
