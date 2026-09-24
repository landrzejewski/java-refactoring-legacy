using Training.Workshop.Shared;

namespace Training.Workshop.M8.S03ParallelRun.Step4;

/// <summary>Krok 4 (bez zmian): stary kalkulator - w trybie Shadow jego wynik jest autorytatywny.</summary>
public sealed class LegacyPriceCalculator
{
    public Money Price(TicketQuery q)
    {
        double p = 0;
        if (q.Format.Equals("2D"))
        {
            p = 25.00;
        }
        else if (q.Format.Equals("3D"))
        {
            p = 32.00;
        }
        else if (q.Format.Equals("IMAX"))
        {
            p = 40.00;
        }
        if (q.Type.Equals("STUDENT"))
        {
            p = p - p * 0.25;
        }
        else if (q.Type.Equals("SENIOR"))
        {
            p = p - p * 0.30;
        }
        else if (q.Type.Equals("CHILD"))
        {
            p = p - p * 0.40;
        }
        if (q.Start.Hour < 12)
        {
            p = p - 5;
        }
        if (q.Row >= 10)
        {
            p = p + 10;
        }
        if (q.Format.Equals("3D"))
        {
            p = p + 3;
        }
        return new Money((decimal)p);
    }
}
