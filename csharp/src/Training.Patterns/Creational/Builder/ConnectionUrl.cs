using System.Globalization;

namespace Training.Patterns.Creational.Builder;

public class ConnectionUrl
{
    // Java fields are package-private and written directly by the builders - here: internal setters
    public string? Host { get; internal set; }

    public int Port { get; internal set; }

    public string? Protocol { get; internal set; }

    public string? Database { get; internal set; }

    public string? Encoding { get; internal set; }

    public override string ToString() =>
        string.Format(CultureInfo.InvariantCulture, "jdbc:{0}://{1}:{2}/{3}?encoding={4}",
            Protocol ?? "null", Host ?? "null", Port, Database ?? "null", Encoding ?? "null");
}
