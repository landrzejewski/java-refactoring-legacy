/**
 * Model z systemu dystrybutora (nie zmieniamy go): same napisy i cudze konwencje.
 * Status "A" aktywna / "S" zawieszona, daty "yyyyMMdd", minimalna gwarancja w groszach
 * jako tekst, udział jako "45%" (czasem ze spacjami: " 40 %"), tytuł wielkimi literami.
 */
export class DistributorRecord {
  constructor(
    readonly filmCode: string,
    readonly titleUpper: string,
    readonly statusCode: string,
    readonly validFrom: string,
    readonly validTo: string,
    readonly minGuaranteeCents: string,
    readonly share: string,
  ) {}
}
