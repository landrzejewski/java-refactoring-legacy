namespace Training.Workshop.M5.S10FieldHiding.Step1;

/// <summary>Krok 1: ukrywające pole usunięte; ukrywająca metoda statyczna jeszcze zostaje.</summary>
public class StudentTicket : Ticket
{
    public StudentTicket() : base("STUDENT")
    {
    }

    public static new string Category()
    {
        return "BILET ULGOWY";
    }
}
