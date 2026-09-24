using Training.Workshop.Shared;

namespace Training.Workshop.M8.S04ShadowLimits.Step2;

/// <summary>
/// Krok 2: przechwycenie efektów - nagrywa zamiary kandydata w formacie dziennika infrastruktury,
/// niczego nie wysyła, nie obciąża i nie zapisuje.
/// </summary>
public sealed class RecordingEffects : IEffects
{
    private readonly List<string> _recorded = [];

    public void SendMail(string to, string text)
    {
        _recorded.Add("MAIL " + to + ": " + text);
    }

    public void Charge(string card, Money amount)
    {
        _recorded.Add("CHARGE " + card + ": " + amount);
    }

    public void Save(string row)
    {
        _recorded.Add("SAVE " + row);
    }

    public IReadOnlyList<string> Recorded()
    {
        return _recorded.ToList();
    }
}
