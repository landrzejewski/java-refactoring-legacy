namespace Training.Workshop.M6.S13ExtractComposite.Step1;

/// <summary>Krok 1: bez zmian - liść - pojedynczy film.</summary>
public sealed record Film(string Title, int Minutes) : IProgramItem
{
    public string Describe()
    {
        return Title + " (" + Minutes + " min)";
    }
}
