using Training.Workshop.Shared;

namespace Training.Workshop.M5.S31AntiCorruptionLayer.Step3;

/// <summary>
/// Krok 3: bez zmian - licencja filmu w języku kina - typy domeny (DateOnly, Money, procent jako int),
/// niezmienniki w konstruktorze i reguły przy danych: czy obejmuje dzień, ile się należy.
/// </summary>
public sealed record FilmLicense
{
    public FilmLicense(string filmCode, string title, bool active, DateOnly validFrom, DateOnly validTo,
        int sharePercent, Money minimumGuarantee)
    {
        ArgumentNullException.ThrowIfNull(filmCode);
        ArgumentNullException.ThrowIfNull(title);
        ArgumentNullException.ThrowIfNull(minimumGuarantee);
        if (validTo < validFrom)
        {
            throw new ArgumentException("license ends before it starts");
        }
        if (sharePercent < 0 || sharePercent > 100)
        {
            throw new ArgumentException("share out of range: " + sharePercent);
        }
        if (minimumGuarantee.CompareTo(Money.Zero) < 0)
        {
            throw new ArgumentException("minimum guarantee must not be negative");
        }
        FilmCode = filmCode;
        Title = title;
        Active = active;
        ValidFrom = validFrom;
        ValidTo = validTo;
        SharePercent = sharePercent;
        MinimumGuarantee = minimumGuarantee;
    }

    public string FilmCode { get; }

    public string Title { get; }

    public bool Active { get; }

    public DateOnly ValidFrom { get; }

    public DateOnly ValidTo { get; }

    public int SharePercent { get; }

    public Money MinimumGuarantee { get; }

    public bool Covers(DateOnly day)
    {
        return day >= ValidFrom && day <= ValidTo;
    }

    public bool AllowsScreeningOn(DateOnly day)
    {
        return Active && Covers(day);
    }

    /// <summary>Udział dystrybutora, ale nie mniej niż minimalna gwarancja.</summary>
    public Money DueFor(Money revenue)
    {
        return revenue.Percent(SharePercent).Max(MinimumGuarantee);
    }
}
