using Training.Workshop.M3.S11Dip.Step3.App;
using Training.Workshop.M3.S11Dip.Step3.Infra;

namespace Training.Workshop.M3.S11Dip.Step3;

/// <summary>Composition root wariantu: jedyne miejsce, które zna adapter i składa graf obiektów.</summary>
public static class Main
{
    public static ConfirmReservation ConfirmReservation()
    {
        return new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender("smtp.kino.pl", 25)));
    }
}
