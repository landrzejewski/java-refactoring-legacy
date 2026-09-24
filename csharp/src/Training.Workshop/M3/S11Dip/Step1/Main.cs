using Training.Workshop.M3.S11Dip.Step1.App;
using Training.Workshop.M3.S11Dip.Step1.Infra;

namespace Training.Workshop.M3.S11Dip.Step1;

/// <summary>Composition root wariantu: jedyne miejsce, które składa graf obiektów.</summary>
public static class Main
{
    public static ConfirmReservation ConfirmReservation()
    {
        return new ConfirmReservation(new SmtpMailSender("smtp.kino.pl", 25));
    }
}
