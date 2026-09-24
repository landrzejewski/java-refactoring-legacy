namespace Training.Workshop.M6.S13ExtractComposite.Step2;

/// <summary>Krok 2: blok krótkich metraży = suma dzieci, bez przerw.</summary>
public sealed class ShortsBlock : CompositeProgramItem
{
    public ShortsBlock(string name)
        : base(name)
    {
    }

    public override int Minutes => ChildrenMinutes();

    protected override string Label()
    {
        return "Blok";
    }
}
