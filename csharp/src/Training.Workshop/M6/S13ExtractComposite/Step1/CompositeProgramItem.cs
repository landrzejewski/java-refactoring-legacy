namespace Training.Workshop.M6.S13ExtractComposite.Step1;

/// <summary>Krok 1: Extract Superclass - wspólna obsługa dzieci (pole, Add, Children) w jednym miejscu.</summary>
public abstract class CompositeProgramItem : IProgramItem
{
    private readonly List<IProgramItem> _children = [];

    public void Add(IProgramItem child)
    {
        ArgumentNullException.ThrowIfNull(child);
        _children.Add(child);
    }

    public IReadOnlyList<IProgramItem> Children => _children.ToList().AsReadOnly();

    public abstract int Minutes { get; }

    public abstract string Describe();
}
