using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step1;

/// <summary>Krok 1: adapter portu na prawdziwą infrastrukturę (poczta, płatności, baza).</summary>
public sealed class RealEffects : IEffects
{
    private readonly Infrastructure _infra;

    public RealEffects(Infrastructure infra)
    {
        _infra = infra;
    }

    public void SendMail(string to, string text)
    {
        _infra.SendMail(to, text);
    }

    public void Charge(string card, Money amount)
    {
        _infra.Charge(card, amount);
    }

    public void Save(string row)
    {
        _infra.Save(row);
    }
}
