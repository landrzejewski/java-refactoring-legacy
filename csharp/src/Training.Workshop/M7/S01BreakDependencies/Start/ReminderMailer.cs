namespace Training.Workshop.M7.S01BreakDependencies.Start;

/// <summary>Statyczny klient SMTP. Poza produkcją serwer jest nieosiągalny.</summary>
public static class ReminderMailer
{
    public static void Send(string to, string subject, string body)
    {
        throw new InvalidOperationException("SMTP smtp.kino.pl niedostepny (" + to + ")");
    }
}
