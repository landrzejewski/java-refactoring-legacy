namespace Training.Workshop.M6.S13ExtractComposite.Start;

/// <summary>Start: liść - pojedynczy film.</summary>
public sealed record Film(string Title, int Minutes) : IProgramItem
{
    public string Describe()
    {
        return Title + " (" + Minutes + " min)";
    }
}
