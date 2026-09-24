namespace Training.Workshop.M5.S05ExtractSubclass.Step2;

/// <summary>Krok 2: nowa podklasa - na razie pusta, tylko przekazuje flagę i gościa do bazy.</summary>
public sealed class PremiereScreening : Screening
{
    internal PremiereScreening(string title, string format, string guest) : base(title, format, true, guest)
    {
    }
}
