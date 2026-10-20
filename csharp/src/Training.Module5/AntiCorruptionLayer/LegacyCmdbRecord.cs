namespace Training.Module5.AntiCorruptionLayer;

/// <summary>
/// Model starego CMDB: kryptyczne kody środowisk (<c>"P"/"T"/"D"</c>), status jako
/// liczba (<c>1</c> aktywne, <c>0</c> wyłączone, <c>9</c> wycofane) i data ostatniego
/// wdrożenia jako tekst <c>yyyyMMdd</c>.
/// </summary>
public sealed record LegacyCmdbRecord(string Nm, string EnvCd, int StatFlg, string LstDplyDt);
