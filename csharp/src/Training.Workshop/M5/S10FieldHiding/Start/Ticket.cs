namespace Training.Workshop.M5.S10FieldHiding.Start;

/// <summary>
/// Start: pole <c>Type</c> i metoda statyczna <c>Category()</c> wyglądają na "nadpisywalne", ale nie są.
/// Label() jest skompilowane w Ticket, więc czyta pole Ticket.Type i woła Ticket.Category() -
/// także dla obiektu StudentTicket.
/// </summary>
public class Ticket
{
    public string Type = "NORMAL";

    public static string Category()
    {
        return "BILET";
    }

    public string Label()
    {
        return Category() + ": " + Type;
    }
}
