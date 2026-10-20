namespace Training.Workshop.M3.S03BehaviourVector.Start;

/// <summary>Statyczny klient SMTP. Z testu nie widać, co i do kogo wysłał.</summary>
public static class CinemaMailer
{
    public static void Send(string to, string text)
    {
        // produkcyjnie: SMTP smtp.kino.pl
    }
}
