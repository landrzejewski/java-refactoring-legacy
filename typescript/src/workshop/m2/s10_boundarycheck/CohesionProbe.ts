import { readFileSync } from 'node:fs';

/** Wynik diagnostyki: LCOM4 i spójne grupy metod (nazwy posortowane). */
export class CohesionResult {
  constructor(readonly lcom4: number, readonly groups: readonly string[]) {}

  toString(): string {
    return `Result[lcom4=${this.lcom4}, groups=[${this.groups.join(', ')}]]`;
  }
}

/**
 * Narzędzie sceny: prosta diagnostyka spójności w stylu LCOM4. Metody są połączone,
 * gdy używają wspólnego pola instancji albo jedna woła drugą. LCOM4 = liczba spójnych
 * grup metod; wynik większy niż 1 podpowiada, że w klasie mieszkają dwa pojęcia.
 *
 * To heurystyka na źródle w stylu tego repozytorium (pola i właściwości parametrów
 * konstruktora, składowe wcięte o 2 spacje) - sygnał do rozmowy, nie wyrocznia.
 * Konstruktor pomijamy (jego parametry-właściwości traktujemy jak pola).
 */
export class CohesionProbe {
  private static readonly FIELD =
    /^ {2}(?!static\b)(?:(?:private|protected|public)\s+)?(?:readonly\s+)?(\w+)[?!]?\s*[:=].*;\s*$/;
  private static readonly METHOD =
    /^ {2}(?:(?:public|private|protected)\s+)?(?:static\s+)?(\w+)\s*\(.*\)(?:\s*:\s*[^{]+)?\s*\{\s*$/;
  private static readonly CONSTRUCTOR = /^ {2}constructor\s*\(/;
  private static readonly PARAMETER_PROPERTY = /(?:private|protected|public|readonly)\s+(?:readonly\s+)?(\w+)\s*[?:]/g;

  analyze(sourceFile: string): CohesionResult {
    const lines = readFileSync(sourceFile, 'utf8').split('\n');
    const fields = new Set<string>();
    const bodies = new Map<string, string>();
    for (let i = 0; i < lines.length; i++) {
      const line = lines[i] ?? '';
      const field = CohesionProbe.FIELD.exec(line);
      const method = CohesionProbe.METHOD.exec(line);
      if (CohesionProbe.CONSTRUCTOR.test(line)) {
        const { text, next } = CohesionProbe.block(lines, i);
        const signature = text.slice(0, text.indexOf('{'));
        for (const m of signature.matchAll(CohesionProbe.PARAMETER_PROPERTY)) {
          fields.add(m[1] ?? '');
        }
        i = next - 1;
      } else if (field !== null) {
        fields.add(field[1] ?? '');
      } else if (method !== null) {
        const name = method[1] ?? '';
        const { text, next } = CohesionProbe.block(lines, i);
        bodies.set(name, (bodies.get(name) ?? '') + text);
        i = next - 1;
      }
    }
    return CohesionProbe.groups(fields, bodies);
  }

  /** Tekst od wiersza start do zamknięcia nawiasów klamrowych i indeks następnego wiersza. */
  private static block(lines: readonly string[], start: number): { text: string; next: number } {
    let text = '';
    let depth = 0;
    let opened = false;
    let j = start;
    do {
      const line = lines[j++] ?? '';
      depth += CohesionProbe.count(line, '{') - CohesionProbe.count(line, '}');
      opened ||= line.includes('{');
      text += `${line}\n`;
    } while ((!opened || depth > 0) && j < lines.length);
    return { text, next: j };
  }

  private static groups(fields: ReadonlySet<string>, bodies: ReadonlyMap<string, string>): CohesionResult {
    const methods = [...bodies.keys()];
    const parent = new Map<string, string>(methods.map((m) => [m, m]));
    for (const a of methods) {
      for (const b of methods) {
        const bodyA = bodies.get(a) ?? '';
        const bodyB = bodies.get(b) ?? '';
        const connected = CohesionProbe.calls(bodyA, b) || CohesionProbe.sharesField(fields, bodyA, bodyB);
        if (a !== b && connected) {
          parent.set(CohesionProbe.find(parent, a), CohesionProbe.find(parent, b));
        }
      }
    }
    const components = new Map<string, string[]>();
    for (const m of methods) {
      const root = CohesionProbe.find(parent, m);
      components.set(root, [...(components.get(root) ?? []), m]);
    }
    const groups = [...components.values()].map((g) => g.sort().join(', ')).sort();
    return new CohesionResult(groups.length, groups);
  }

  private static calls(body: string, method: string): boolean {
    return new RegExp(`\\b${method}\\s*\\(`).test(body.slice(body.indexOf('{')));
  }

  private static sharesField(fields: ReadonlySet<string>, a: string, b: string): boolean {
    return [...fields].some((f) => CohesionProbe.uses(a, f) && CohesionProbe.uses(b, f));
  }

  private static uses(body: string, field: string): boolean {
    return new RegExp(`\\b${field}\\b`).test(body.slice(body.indexOf('{')));
  }

  private static find(parent: ReadonlyMap<string, string>, node: string): string {
    let root = node;
    while (parent.get(root) !== root) {
      root = parent.get(root) ?? root;
    }
    return root;
  }

  private static count(line: string, c: string): number {
    return [...line].filter((ch) => ch === c).length;
  }
}
