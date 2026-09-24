using System.Globalization;

namespace Training.Workshop.M7.S06ParameterObject.Step2;

/// <summary>Krok 2: Move Method - typ przyciąga zachowanie: opis terminu należy do terminu.</summary>
public sealed record ScreeningSlot(string ScreeningId, DateOnly Date, int Hall, string Format)
{
    public string Label()
    {
        return ScreeningId + " " + Date.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
            + " sala " + Hall + " (" + Format + ")";
    }
}
