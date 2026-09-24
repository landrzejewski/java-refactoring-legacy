using Training.Workshop.Shared;

namespace Training.Workshop.M6.S04EncapsulateFactory.Step3.Ticketing;

public static partial class Tickets
{
    /// <summary>Krok 3: prywatny typ fabryki - poza Tickets nie da się go utworzyć ani nazwać.</summary>
    private sealed record StandardTicket(string Title, Money Base, int Row) : ITicket
    {
        public Money Price()
        {
            return Base;
        }

        public string Describe()
        {
            return Title + " r" + Row + " " + Price();
        }
    }
}
