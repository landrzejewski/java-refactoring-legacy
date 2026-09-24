using Training.Workshop.M3.S11Dip;

namespace Training.Workshop.Tests.M3.S11Dip;

/// <summary>Potwierdzenie działa tak samo; od kroku 1 sprawdzamy też, co faktycznie poszło przez SMTP.</summary>
public sealed class S11EquivalenceTest
{
    private static readonly Reservation Dune =
        new("anna@kino.pl", "Diuna", new DateTime(2026, 10, 2, 20, 0, 0), 2);
    private static readonly Reservation BadEmail =
        new("jan-kino.pl", "Amator", new DateTime(2026, 10, 3, 18, 30, 0), 1);
    private static readonly Reservation NoSeats =
        new("jan@kino.pl", "Amator", new DateTime(2026, 10, 3, 18, 30, 0), 0);

    private static readonly Support.Scene<Reservation, string> Confirmations = Support.Scene.Variants<Reservation, string>()
        .Variant("start", Safe(Training.Workshop.M3.S11Dip.Start.Main.ConfirmReservation().Confirm))
        .Variant("step1", Safe(Training.Workshop.M3.S11Dip.Step1.Main.ConfirmReservation().Confirm))
        .Variant("step2", Safe(Training.Workshop.M3.S11Dip.Step2.Main.ConfirmReservation().Confirm))
        .Variant("step3", Safe(Training.Workshop.M3.S11Dip.Step3.Main.ConfirmReservation().Confirm))
        .Expect("poprawna rezerwacja", Dune, "potwierdzono: anna@kino.pl")
        .Expect("serwer odrzuca adres", BadEmail, "blad: SMTP odrzucil: 550 mailbox unavailable")
        .Expect("rezerwacja bez miejsc", NoSeats, "blad: rezerwacja bez miejsc");

    private static readonly Support.Scene<Reservation, string> Messages = Support.Scene.Variants<Reservation, string>()
        .Variant("step1", r =>
        {
            var smtp = new Training.Workshop.M3.S11Dip.Step1.Infra.SmtpMailSender("smtp.kino.pl", 25);
            new Training.Workshop.M3.S11Dip.Step1.App.ConfirmReservation(smtp).Confirm(r);
            return string.Join("|", smtp.Transcript);
        })
        .Variant("step2", r =>
        {
            var smtp = new Training.Workshop.M3.S11Dip.Step2.Infra.SmtpMailSender("smtp.kino.pl", 25);
            new Training.Workshop.M3.S11Dip.Step2.App.ConfirmReservation(smtp).Confirm(r);
            return string.Join("|", smtp.Transcript);
        })
        .Variant("step3", r =>
        {
            var smtp = new Training.Workshop.M3.S11Dip.Step3.Infra.SmtpMailSender("smtp.kino.pl", 25);
            new Training.Workshop.M3.S11Dip.Step3.App.ConfirmReservation(
                new Training.Workshop.M3.S11Dip.Step3.Infra.SmtpCustomerNotifier(smtp)).Confirm(r);
            return string.Join("|", smtp.Transcript);
        })
        .Expect("MIME dla Diuny", Dune, "smtp.kino.pl:25 To: anna@kino.pl\r\nSubject: Rezerwacja\r\n\r\n"
            + "Rezerwacja: Diuna, 2026-10-02T20:00, miejsc: 2. Zaplac w ciagu 15 minut.");

    public static TheoryData<string> ConfirmationCases => Confirmations.Tests();

    public static TheoryData<string> MessageCases => Messages.Tests();

    [Theory]
    [MemberData(nameof(ConfirmationCases))]
    public void EveryStepConfirmsTheSame(string test) => Confirmations.Run(test);

    [Theory]
    [MemberData(nameof(MessageCases))]
    public void StepsSendTheSameMimeMessage(string test) => Messages.Run(test);

    private static Func<Reservation, string> Safe(Func<Reservation, string> confirm)
    {
        return r =>
        {
            try
            {
                return confirm(r);
            }
            catch (Exception e)
            {
                return "blad: " + e.Message;
            }
        };
    }
}
