namespace Training.Workshop.M7.S15BehaviourVector.Step2;

/// <summary>Statyczny klient SMTP. Z testu nie widać, co i do kogo wysłał.</summary>
public static class CinemaMailer
{
    public static void Send(string to, string text)
    {
        // produkcyjnie: SMTP smtp.kino.pl
    }
}
