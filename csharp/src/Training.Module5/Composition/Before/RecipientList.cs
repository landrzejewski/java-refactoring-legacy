namespace Training.Module5.Composition.Before;

// Inherits the whole List<string> API (Insert, Sort, Clear, RemoveAll, indexer set, ...)
// although clients only need a handful of operations.
public sealed class RecipientList : List<string>
{
    public IReadOnlyList<string> Snapshot()
    {
        return new List<string>(this).AsReadOnly();
    }
}
