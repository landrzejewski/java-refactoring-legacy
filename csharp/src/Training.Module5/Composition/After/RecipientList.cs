using System.Collections;

namespace Training.Module5.Composition.After;

public sealed class RecipientList : IEnumerable<string>
{
    private readonly List<string> _recipients = [];

    public int Count => _recipients.Count;

    public void Add(string recipient)
    {
        _recipients.Add(recipient);
    }

    public bool Remove(string recipient)
    {
        return _recipients.Remove(recipient);
    }

    public bool Contains(string recipient)
    {
        return _recipients.Contains(recipient);
    }

    public IEnumerator<string> GetEnumerator()
    {
        return _recipients.AsReadOnly().GetEnumerator();
    }

    IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();

    public IReadOnlyList<string> Snapshot()
    {
        return new List<string>(_recipients).AsReadOnly();
    }
}
