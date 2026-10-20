/**
 * Model starego CMDB: kryptyczne kody środowisk ("P"/"T"/"D"), status jako
 * liczba (1 aktywne, 0 wyłączone, 9 wycofane) i data ostatniego
 * wdrożenia jako tekst yyyyMMdd.
 */
export class LegacyCmdbRecord {
  constructor(
    readonly nm: string,
    readonly envCd: string,
    readonly statFlg: number,
    readonly lstDplyDt: string,
  ) {}
}
