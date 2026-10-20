using Training.Workshop.Shared;

namespace Training.Workshop.M3.S05ShadowLimits.Step1;

/// <summary>Krok 1: port efektów ubocznych - nowa ścieżka nie sięga już bezpośrednio do infrastruktury.</summary>
public interface IEffects
{
    void SendMail(string to, string text);

    void Charge(string card, Money amount);

    void Save(string row);
}
