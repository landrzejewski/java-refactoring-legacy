/** Próbka czystego kodu domeny: bramka nie może tu niczego zgłosić (brak fałszywych alarmów). */
export class PriceTable {
  private static readonly VIP_FROM_ROW = 10;

  basePrice(format: string): number {
    switch (format) {
      case 'IMAX': return 40;
      case '3D': return 32;
      default: return 25;
    }
  }

  vipSurcharge(row: number): number {
    return row >= PriceTable.VIP_FROM_ROW ? 10 : 0;
  }
}
