namespace Training.Patterns.Structural.Flyweight;

public static class TreeFactory
{
    private static readonly Dictionary<string, TreeType> TYPES = [];
    private static readonly Lock TypesLock = new();

    public static TreeType GetTreeType(string name, string color)
    {
        // Java: HashMap.computeIfAbsent; the lock keeps the cache safe when tests run in parallel
        lock (TypesLock)
        {
            var key = name + ":" + color;
            if (!TYPES.TryGetValue(key, out var type))
            {
                type = new TreeType(name, color);
                TYPES[key] = type;
            }
            return type;
        }
    }
}
