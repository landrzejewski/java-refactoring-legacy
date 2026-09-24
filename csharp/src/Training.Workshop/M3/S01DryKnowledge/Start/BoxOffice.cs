namespace Training.Workshop.M3.S01DryKnowledge.Start;

/// <summary>
/// Start: reguła ceny biletu (cena formatu, zniżka typu, seans poranny) jest zapisana
/// DWA razy - w sprzedaży i w zwrocie. Tekst jest inny (switch kontra if i mnożniki),
/// więc detektor duplikatów w IDE nic nie znajdzie, ale wiedza jest ta sama:
/// zmiana zniżki studenckiej wymaga zgodnej edycji dwóch miejsc.
/// </summary>
public sealed class BoxOffice
{
    public decimal Sell(Ticket ticket)
    {
        var @base = ticket.Format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        var discount = ticket.Type switch
        {
            "STUDENT" => 25,
            "SENIOR" => 30,
            "CHILD" => 40,
            _ => 0,
        };
        var price = @base - Math.Round(@base * discount / 100, 2, MidpointRounding.AwayFromZero);
        if (ticket.Start.Hour < 12)
        {
            price -= 5.00m;
        }
        return Math.Round(price, 2, MidpointRounding.AwayFromZero);
    }

    public decimal Refund(Ticket ticket, long hoursBeforeStart)
    {
        // ile kosztował bilet
        var paid = 25.00m;
        if (ticket.Format == "3D")
        {
            paid = 32.00m;
        }
        else if (ticket.Format == "IMAX")
        {
            paid = 40.00m;
        }
        if (ticket.Type == "STUDENT")
        {
            paid *= 0.75m;
        }
        else if (ticket.Type == "SENIOR")
        {
            paid *= 0.70m;
        }
        else if (ticket.Type == "CHILD")
        {
            paid *= 0.60m;
        }
        if (ticket.Start < new TimeOnly(12, 0))
        {
            paid -= 5.00m;
        }
        // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
        var percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
        var refund = Math.Round(paid * percent / 100, 2, MidpointRounding.AwayFromZero) - 3.00m;
        return Math.Round(Math.Max(refund, 0m), 2, MidpointRounding.AwayFromZero);
    }
}
