package pl.training.workshop.m3.s11_dip.step3.infra;

import pl.training.workshop.m3.s11_dip.step3.app.CustomerNotifier;

/**
 * Krok 3: adapter - implementuje port polityki (import infra -&gt; app) i tłumaczy
 * go na protokół: składa MIME, interpretuje kod SMTP, zamienia go na błąd kontraktu.
 */
public final class SmtpCustomerNotifier implements CustomerNotifier {
    private final SmtpMailSender mail;

    public SmtpCustomerNotifier(SmtpMailSender mail) {
        this.mail = mail;
    }

    @Override
    public void notifyCustomer(String email, String message) {
        String mime = "To: " + email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
        String reply = mail.send(email, mime);
        if (!reply.startsWith("250")) {
            throw new IllegalStateException("SMTP odrzucil: " + reply);
        }
    }
}
