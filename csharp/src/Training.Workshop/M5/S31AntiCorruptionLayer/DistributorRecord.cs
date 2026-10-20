namespace Training.Workshop.M5.S31AntiCorruptionLayer;

/// <summary>
/// Model z systemu dystrybutora (nie zmieniamy go): same napisy i cudze konwencje.
/// Status "A" aktywna / "S" zawieszona, daty "yyyyMMdd", minimalna gwarancja w groszach
/// jako tekst, udział jako "45%" (czasem ze spacjami: " 40 %"), tytuł wielkimi literami.
/// </summary>
public sealed record DistributorRecord(string FilmCode, string TitleUpper, string StatusCode,
    string ValidFrom, string ValidTo, string MinGuaranteeCents, string Share);
