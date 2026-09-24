using System.Globalization;

namespace Training.Workshop.M3.S12CleanArchitecture.Start;

/// <summary>
/// Start: kontroler "HTTP" robi wszystko - parsuje parametry, liczy cenę, zapisuje
/// wiersz object[], publikuje komunikat i buduje odpowiedź. Reguły biznesowe
/// (cena, VIP, "najpierw zapis, potem powiadomienie") są splecione z formatem
/// żądania i kolumnami tabeli. Przypadku użycia nie da się wywołać bez HTTP i bazy.
/// </summary>
public sealed class ReservationController
{
    private readonly RowStore _db;
    private readonly Outbox _outbox;

    public ReservationController(RowStore db, Outbox outbox)
    {
        _db = db;
        _outbox = outbox;
    }

    public string Handle(IReadOnlyDictionary<string, string> parameters)
    {
        var email = parameters.GetValueOrDefault("email");
        if (string.IsNullOrWhiteSpace(email))
        {
            return "400 brak email";
        }
        var format = parameters.GetValueOrDefault("format", "2D");
        var rows = parameters.GetValueOrDefault("rows", "").Split(',')
            .Where(s => !string.IsNullOrWhiteSpace(s)).Select(s => int.Parse(s, CultureInfo.InvariantCulture)).ToList();
        if (rows.Count == 0)
        {
            return "400 brak miejsc";
        }
        var @base = format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        var total = 0.00m;
        foreach (var row in rows)
        {
            total += @base;
            if (row >= 10)
            {
                total += 10.00m;
            }
        }
        string id;
        try
        {
            id = _db.Insert([email, format, rows.Count, total]);
        }
        catch (InvalidOperationException e)
        {
            return "503 " + e.Message;
        }
        _outbox.Publish("reservation-created",
            id + ";" + email + ";" + total.ToString(CultureInfo.InvariantCulture));
        return "201 " + id + " " + total.ToString(CultureInfo.InvariantCulture);
    }
}
