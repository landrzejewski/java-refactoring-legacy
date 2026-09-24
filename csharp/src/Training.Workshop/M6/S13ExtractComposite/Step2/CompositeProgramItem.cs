namespace Training.Workshop.M6.S13ExtractComposite.Step2;

/// <summary>
/// Krok 2: Pull Up Method - suma czasu dzieci i szkielet opisu też w nadklasie. Podklasy
/// dostarczają tylko to, czym naprawdę się różnią: etykietę i regułę przerw.
/// </summary>
public abstract class CompositeProgramItem : IProgramItem
{
    private readonly string _name;
    private readonly List<IProgramItem> _children = [];

    protected CompositeProgramItem(string name)
    {
        _name = name;
    }

    public void Add(IProgramItem child)
    {
        ArgumentNullException.ThrowIfNull(child);
        _children.Add(child);
    }

    public IReadOnlyList<IProgramItem> Children => _children.ToList().AsReadOnly();

    public abstract int Minutes { get; }

    protected int ChildrenMinutes()
    {
        var total = 0;
        foreach (var child in _children)
        {
            total += child.Minutes;
        }
        return total;
    }

    public string Describe()
    {
        return Label() + " " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", _children.Select(child => child.Describe())) + "]";
    }

    protected abstract string Label();
}
