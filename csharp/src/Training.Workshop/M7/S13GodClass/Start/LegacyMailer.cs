namespace Training.Workshop.M7.S13GodClass.Start;

/// <summary>"Serwer SMTP" starego systemu - statyczna skrzynka nadawcza.</summary>
public static class LegacyMailer
{
    public static readonly List<string> Sent = [];

    public static void Send(string to, string subject, string body)
    {
        Sent.Add("MAIL to=" + to + " subject=" + subject + " body=" + body);
    }

    public static void Sms(string phone, string text)
    {
        Sent.Add("SMS to=" + phone + " text=" + text);
    }
}
