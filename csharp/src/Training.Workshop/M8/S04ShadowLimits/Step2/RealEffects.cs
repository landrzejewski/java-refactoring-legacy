using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step2;

/// <summary>Krok 2 (bez zmian): adapter portu na prawdziwą infrastrukturę.</summary>
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
