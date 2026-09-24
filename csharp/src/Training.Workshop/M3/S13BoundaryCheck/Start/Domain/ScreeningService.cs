using System.Data.SqlTypes;
using Training.Workshop.M3.S13BoundaryCheck.Start.Adapter;

namespace Training.Workshop.M3.S13BoundaryCheck.Start.Domain;

/// <summary>
/// Start: klasa domeny miesza politykę cenową z mapowaniem na wiersz bazy.
/// Naruszenie granicy: Domain używa Adapter i System.Data.SqlTypes. Diagnostyka spójności:
/// dwie grupy metod na rozłącznych polach (LCOM4 = 2) - dwa pojęcia w jednej klasie.
/// </summary>
public sealed class ScreeningService
{
    private readonly decimal _basePrice;
    private readonly decimal _morningDiscount;
    private readonly string _table;

    public ScreeningService(decimal basePrice, decimal morningDiscount, string table)
    {
        _basePrice = basePrice;
        _morningDiscount = morningDiscount;
        _table = table;
    }

    public decimal Price(Screening screening)
    {
        return IsMorning(screening) ? _basePrice - _morningDiscount : _basePrice;
    }

    private static bool IsMorning(Screening screening)
    {
        return screening.Start.Hour < 12;
    }

    public ScreeningRow ToRow(Screening screening)
    {
        return new ScreeningRow(_table, screening.Title, new SqlDateTime(screening.Start));
    }

    public Screening FromRow(ScreeningRow row)
    {
        if (row.Table != _table)
        {
            throw new ArgumentException("obca tabela: " + row.Table);
        }
        return new Screening(row.Title, row.Start.Value);
    }
}
