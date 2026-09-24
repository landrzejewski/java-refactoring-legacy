namespace Training.Workshop.M5.S10FieldHiding.Start;

/// <summary>
/// Start: podklasa UKRYWA pole i metodę statyczną bazy. W obiekcie są teraz dwa niezależne sloty "Type";
/// który zobaczysz, zależy od typu referencji, a nie od klasy obiektu. C# wymaga tu słowa <c>new</c>
/// (bez niego ostrzeżenie CS0108) - ono tylko ucisza kompilator, niczego nie nadpisuje.
/// </summary>
public class StudentTicket : Ticket
{
    public new string Type = "STUDENT";

    public static new string Category()
    {
        return "BILET ULGOWY";
    }
}
