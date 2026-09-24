namespace Training.Workshop.M6.S13ExtractComposite.Start;

/// <summary>Start: drugi kontener z tą samą obsługą dzieci - filmy krótkie lecą bez przerw.</summary>
public sealed class ShortsBlock : IProgramItem
{
    private readonly string _name;
    private readonly List<IProgramItem> _children = [];

    public ShortsBlock(string name)
    {
        _name = name;
    }

    public void Add(IProgramItem child)
    {
        ArgumentNullException.ThrowIfNull(child);
        _children.Add(child);
    }

    public IReadOnlyList<IProgramItem> Children => _children.ToList().AsReadOnly();

    public int Minutes
    {
        get
        {
            var total = 0;
            foreach (var child in _children)
            {
                total += child.Minutes;
            }
            return total;
        }
    }

    public string Describe()
    {
        return "Blok " + _name + " (" + Minutes + " min) "
            + "[" + string.Join(", ", _children.Select(child => child.Describe())) + "]";
    }
}
