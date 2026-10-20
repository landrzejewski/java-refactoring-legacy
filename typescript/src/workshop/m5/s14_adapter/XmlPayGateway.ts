/**
 * "Biblioteka" starej bramki (nie zmieniamy jej): XML jako tekst, kwota w groszach.
 * Deterministyczna symulacja: powyżej 500.00 odmowa z kodem 51.
 */
export class XmlPayGateway {
  private static readonly CHARGE = /^<charge ref='([^']+)' amount='(\d+)'\/>$/;

  submit(xml: string): string {
    const matcher = XmlPayGateway.CHARGE.exec(xml);
    if (matcher === null) {
      return "<result status='ERROR' code='XML'/>";
    }
    if (Number.parseInt(matcher[2] ?? '', 10) > 50_000) {
      return "<result status='DECLINED' code='51'/>";
    }
    return `<result status='OK' id='X-${matcher[1] ?? ''}'/>`;
  }
}
