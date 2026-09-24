namespace Training.Workshop.M3.S11Dip.Step3.Infra;

/// <summary>
/// Szczegół techniczny: klient SMTP (symulowany). API mówi językiem protokołu:
/// surowa wiadomość MIME na wejściu, kod odpowiedzi SMTP na wyjściu.
/// </summary>
public sealed class SmtpMailSender
{
    private readonly string _host;
    private readonly int _port;
    private readonly List<string> _transcript = [];

    public SmtpMailSender(string host, int port)
    {
        _host = host;
        _port = port;
    }

    /// <summary>Symulacja: adres bez '@' daje 550, poprawny 250.</summary>
    public string Send(string to, string mimeMessage)
    {
        if (!to.Contains('@'))
        {
            return "550 mailbox unavailable";
        }
        _transcript.Add(_host + ":" + _port + " " + mimeMessage);
        return "250 OK";
    }

    public IReadOnlyList<string> Transcript => _transcript.ToList();
}
