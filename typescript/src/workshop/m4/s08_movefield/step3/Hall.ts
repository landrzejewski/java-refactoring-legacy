/** Sala kinowa - właściciel progu VIP i pytania "czy ten rząd jest VIP". */
export class Hall {
  constructor(readonly name: string, private readonly vipFromRow: number) {}

  isVip(row: number): boolean {
    return row >= this.vipFromRow;
  }
}
