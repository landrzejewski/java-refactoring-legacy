namespace Training.Workshop.M5.S10FieldHiding.Step2;

/// <summary>Krok 2: prawdziwy override - kompilator pilnuje, że coś nadpisujemy.</summary>
public class StudentTicket : Ticket
{
    public StudentTicket() : base("STUDENT")
    {
    }

    public override string Category()
    {
        return "BILET ULGOWY";
    }
}
