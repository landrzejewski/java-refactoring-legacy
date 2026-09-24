using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step3;

/// <summary>
/// Krok 3: wynik czystego obliczenia - odpowiedź dla klienta i lista efektów DO wykonania.
/// Efekty to dane: można je porównać, zalogować albo wykonać, ale plan sam niczego nie robi.
/// </summary>
public sealed record BookingPlan(string Result, IReadOnlyList<BookingPlan.IEffect> Effects)
{
    public interface IEffect
    {
        string Describe();

        void ApplyTo(IEffects port);
    }

    public sealed record Charge(string Card, Money Amount) : IEffect
    {
        public string Describe()
        {
            return "CHARGE " + Card + ": " + Amount;
        }

        public void ApplyTo(IEffects port)
        {
            port.Charge(Card, Amount);
        }
    }

    public sealed record Save(string Row) : IEffect
    {
        public string Describe()
        {
            return "SAVE " + Row;
        }

        public void ApplyTo(IEffects port)
        {
            port.Save(Row);
        }
    }

    public sealed record SendMail(string To, string Text) : IEffect
    {
        public string Describe()
        {
            return "MAIL " + To + ": " + Text;
        }

        public void ApplyTo(IEffects port)
        {
            port.SendMail(To, Text);
        }
    }
}
