/**
 * "Biblioteka" starego terminala płatniczego (nie zmieniamy jej): kwota w groszach, odpowiedź
 * jako tekst `STATUS;KOD;KWOTA_W_GROSZACH`, np. `OK;AUTH-R1;4500`.
 * Deterministyczna symulacja: kwota <= 0 daje `ERROR;AMOUNT;0`, powyżej 1000.00
 * `DECLINED;51;0`, powyżej 300.00 autoryzację częściową do 300.00.
 */
export class LegacyTerminalClient {
  authorize(bookingId: string, grosze: number): string {
    if (grosze <= 0) {
      return 'ERROR;AMOUNT;0';
    }
    if (grosze > 100_000) {
      return 'DECLINED;51;0';
    }
    return `OK;AUTH-${bookingId};${Math.min(grosze, 30_000)}`;
  }
}
