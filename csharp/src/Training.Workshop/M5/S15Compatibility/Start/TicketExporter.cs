using System.Reflection;
using System.Text;
using Training.Workshop.M5.S15Compatibility;

namespace Training.Workshop.M5.S15Compatibility.Start;

/// <summary>
/// Start: legacy eksporter szuka kolumn tylko w klasie runtime (<c>GetMethods(... | DeclaredOnly)</c>).
/// Działa wyłącznie dlatego, że każda podklasa sama deklaruje Price(). Po Pull Up przestanie.
/// </summary>
public sealed class TicketExporter
{
    public string Export(Ticket ticket)
    {
        var row = new StringBuilder(ticket.Title);
        var candidates = ticket.GetType().GetMethods(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly);
        foreach (var method in candidates
                     .Where(method => method.IsDefined(typeof(ColumnAttribute)))
                     .OrderBy(method => method.GetCustomAttribute<ColumnAttribute>()!.Value, StringComparer.Ordinal))
        {
            row.Append(';')
                .Append(method.GetCustomAttribute<ColumnAttribute>()!.Value)
                .Append('=')
                .Append(Read(method, ticket));
        }
        return row.ToString();
    }

    private static object? Read(MethodInfo method, Ticket ticket)
    {
        try
        {
            return method.Invoke(ticket, null);
        }
        catch (TargetInvocationException e)
        {
            throw new InvalidOperationException(e.Message, e);
        }
    }
}
