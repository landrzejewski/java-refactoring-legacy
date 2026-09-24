using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step2;

/// <summary>Krok 2 (bez zmian): port efektów ubocznych nowej ścieżki.</summary>
public interface IEffects
{
    void SendMail(string to, string text);

    void Charge(string card, Money amount);

    void Save(string row);
}
