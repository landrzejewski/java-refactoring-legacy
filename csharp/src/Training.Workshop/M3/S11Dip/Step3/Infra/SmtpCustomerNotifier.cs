using Training.Workshop.M3.S11Dip.Step3.App;

namespace Training.Workshop.M3.S11Dip.Step3.Infra;

/// <summary>
/// Krok 3: adapter - implementuje port polityki (using Infra -&gt; App) i tłumaczy
/// go na protokół: składa MIME, interpretuje kod SMTP, zamienia go na błąd kontraktu.
/// </summary>
public sealed class SmtpCustomerNotifier : ICustomerNotifier
{
    private readonly SmtpMailSender _mail;

    public SmtpCustomerNotifier(SmtpMailSender mail)
    {
        _mail = mail;
    }

    public void NotifyCustomer(string email, string message)
    {
        var mime = "To: " + email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
        var reply = _mail.Send(email, mime);
        if (!reply.StartsWith("250", StringComparison.Ordinal))
        {
            throw new InvalidOperationException("SMTP odrzucil: " + reply);
        }
    }
}
