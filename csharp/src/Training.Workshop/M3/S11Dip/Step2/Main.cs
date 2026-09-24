using Training.Workshop.M3.S11Dip.Step2.App;
using Training.Workshop.M3.S11Dip.Step2.Infra;

namespace Training.Workshop.M3.S11Dip.Step2;

/// <summary>Composition root wariantu: jedyne miejsce, które składa graf obiektów.</summary>
public static class Main
{
    public static ConfirmReservation ConfirmReservation()
    {
        return new ConfirmReservation(new SmtpMailSender("smtp.kino.pl", 25));
    }
}
