using Training.Workshop.Shared;

namespace Training.Workshop.M3.S05ShadowLimits.Step3;

/// <summary>Krok 3 (bez zmian): port efektów ubocznych - używany tylko przy wykonaniu planu.</summary>
public interface IEffects
{
    void SendMail(string to, string text);

    void Charge(string card, Money amount);

    void Save(string row);
}
