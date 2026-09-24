using System.Text;
using System.Text.RegularExpressions;

namespace Training.Workshop.M3.S13BoundaryCheck;

/// <summary>
/// Narzędzie sceny: prosta diagnostyka spójności w stylu LCOM4. Metody są połączone,
/// gdy używają wspólnego pola instancji albo jedna woła drugą. LCOM4 = liczba spójnych
/// grup metod; wynik większy niż 1 podpowiada, że w klasie mieszkają dwa pojęcia.
/// <para>To heurystyka na źródle w stylu tego repozytorium (pola private, typy bez spacji,
/// metody wcięte o 4 spacje, klamra w osobnej linii albo na końcu sygnatury) - sygnał
/// do rozmowy, nie wyrocznia. Konstruktor pomijamy.</para>
/// </summary>
public sealed class CohesionProbe
{
    private const string Type = @"[\w][\w<>\[\],.?]*";
    private static readonly Regex Field = new(
        @"^ {4}private\s+(?!static)(?!const)(?:readonly\s+)?" + Type + @"\s+(\w+)\s*(?:=.*)?;\s*$");
    private static readonly Regex Method = new(
        @"^ {4}(?:public |private |protected |internal )?(?:static )?" + Type + @"\s+(\w+)\s*\(.*\)\s*\{?\s*$");

    /// <summary>Wynik: LCOM4 i grupy metod (nazwy w grupie po przecinku, grupy posortowane).</summary>
    public sealed record Result(int Lcom4, IReadOnlyList<string> Groups)
    {
        public bool Equals(Result? other) =>
            other is not null && Lcom4 == other.Lcom4 && Groups.SequenceEqual(other.Groups);

        public override int GetHashCode() => Lcom4;

        public override string ToString() => $"Result {{ Lcom4 = {Lcom4}, Groups = [{string.Join("; ", Groups)}] }}";
    }

    public Result Analyze(string sourceFile)
    {
        var lines = File.ReadAllLines(sourceFile);
        var className = Path.GetFileNameWithoutExtension(sourceFile);
        var fields = new List<string>();
        var bodies = new Dictionary<string, string>();
        var order = new List<string>();
        for (var i = 0; i < lines.Length; i++)
        {
            var field = Field.Match(lines[i]);
            var method = Method.Match(lines[i]);
            if (field.Success)
            {
                fields.Add(field.Groups[1].Value);
            }
            else if (method.Success && method.Groups[1].Value != className)
            {
                var body = new StringBuilder();
                var depth = 0;
                var opened = false;
                var j = i;
                do
                {
                    var line = lines[j++];
                    depth += Count(line, '{') - Count(line, '}');
                    opened |= line.Contains('{');
                    body.Append(line).Append('\n');
                }
                while ((!opened || depth > 0) && j < lines.Length);
                var name = method.Groups[1].Value;
                if (!bodies.TryAdd(name, body.ToString()))
                {
                    bodies[name] += body.ToString();
                }
                else
                {
                    order.Add(name);
                }
                i = j - 1;
            }
        }
        return Groups(fields, order, bodies);
    }

    private static Result Groups(List<string> fields, List<string> methods, Dictionary<string, string> bodies)
    {
        var parent = methods.ToDictionary(m => m, m => m);
        foreach (var a in methods)
        {
            foreach (var b in methods)
            {
                var connected = Calls(bodies[a], b) || SharesField(fields, bodies[a], bodies[b]);
                if (a != b && connected)
                {
                    parent[Find(parent, a)] = Find(parent, b);
                }
            }
        }
        var components = new SortedDictionary<string, SortedSet<string>>(StringComparer.Ordinal);
        foreach (var m in methods)
        {
            var root = Find(parent, m);
            if (!components.TryGetValue(root, out var group))
            {
                group = new SortedSet<string>(StringComparer.Ordinal);
                components[root] = group;
            }
            group.Add(m);
        }
        var groups = components.Values
            .Select(g => string.Join(", ", g))
            .Order(StringComparer.Ordinal)
            .ToList();
        return new Result(groups.Count, groups);
    }

    private static bool Calls(string body, string method)
    {
        return Regex.IsMatch(body[body.IndexOf('{')..], @"\b" + method + @"\s*\(");
    }

    private static bool SharesField(List<string> fields, string a, string b)
    {
        return fields.Any(f => Uses(a, f) && Uses(b, f));
    }

    private static bool Uses(string body, string field)
    {
        return Regex.IsMatch(body[body.IndexOf('{')..], @"\b" + field + @"\b");
    }

    private static string Find(Dictionary<string, string> parent, string node)
    {
        var root = node;
        while (parent[root] != root)
        {
            root = parent[root];
        }
        return root;
    }

    private static int Count(string line, char c)
    {
        return line.Count(ch => ch == c);
    }
}
