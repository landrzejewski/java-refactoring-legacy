using System.Globalization;

namespace Training.Module5.AntiCorruptionLayer.After.Acl;

/// <summary>
/// Tłumacz między modelami: jedyne miejsce, które zna kody i formaty CMDB.
/// </summary>
internal static class CmdbTranslator
{
    private const string CmdbDate = "yyyyMMdd";

    internal static Environment ToEnvironment(LegacyCmdbRecord record) =>
        new(
            record.Nm,
            ToTier(record.EnvCd),
            record.StatFlg == 1,
            ToDate(record.LstDplyDt));

    private static Tier ToTier(string? code) => code switch
    {
        "P" => Tier.Production,
        "T" => Tier.Test,
        "D" => Tier.Development,
        _ => throw new ArgumentException("unknown CMDB environment code: " + code)
    };

    private static DateOnly ToDate(string? text)
    {
        if (!DateOnly.TryParseExact(text, CmdbDate, CultureInfo.InvariantCulture, DateTimeStyles.None, out var date))
        {
            throw new ArgumentException("invalid CMDB date: " + text);
        }
        return date;
    }
}
