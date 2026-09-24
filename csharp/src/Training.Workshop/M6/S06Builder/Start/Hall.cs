using System.Text;

namespace Training.Workshop.M6.S06Builder.Start;

/// <summary>Start - mutowalny węzeł: sala z listą seansów.</summary>
public sealed class Hall
{
    private readonly string _name;
    private readonly List<Screening> _screenings = [];

    public Hall(string name)
    {
        _name = name;
    }

    public void Add(Screening screening)
    {
        _screenings.Add(screening);
    }

    public int Size()
    {
        return _screenings.Count;
    }

    public string Render()
    {
        var text = new StringBuilder(_name).Append('\n');
        if (_screenings.Count == 0)
        {
            text.Append("  (brak seansow)\n");
        }
        _screenings.ForEach(screening => text.Append(screening.Render()));
        return text.ToString();
    }
}
