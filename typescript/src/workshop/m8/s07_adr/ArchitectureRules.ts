import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';

/** Reguła z ADR-0007: identyfikator z ADR i zakazany wzorzec w tekście źródła. */
export class Rule {
  static readonly R1_PRICING_WITHOUT_NOTIFICATION = new Rule('ADR-0007/R1', /^import .*['"][^'"]*\/notification\//);
  // Tekstowe przybliżenie "kwoty w number": adnotacja number przy nazwie kwoty.
  static readonly R2_PRICING_USES_MONEY = new Rule('ADR-0007/R2', /\b(?:total|sum|unitPrice|price|amount)\??\s*:\s*number\b/);

  private constructor(readonly id: string, readonly forbidden: RegExp) {}

  static values(): readonly Rule[] {
    return [Rule.R1_PRICING_WITHOUT_NOTIFICATION, Rule.R2_PRICING_USES_MONEY];
  }
}

/**
 * Wykonywalny model decyzji z ADR-0007. Każda reguła ma identyfikator z ADR, więc naruszenie
 * w teście prowadzi wprost do uzasadnienia decyzji. Reguły działają na tekście źródeł
 * (świadome uproszczenie - patrz "Konsekwencje" w ADR).
 */
export class ArchitectureRules {
  private constructor() {}

  /** Naruszenia w katalogu pricing wariantu, np. "ADR-0007/R2 TicketPricing.ts:12". */
  static violations(variantDir: string): string[] {
    const violations: string[] = [];
    for (const file of ArchitectureRules.sourceFiles(path.join(variantDir, 'pricing'))) {
      const lines = readFileSync(file, 'utf8').split(/\r?\n/);
      lines.forEach((rawLine, i) => {
        const line = rawLine.trim();
        if (ArchitectureRules.isComment(line)) {
          return;
        }
        for (const rule of Rule.values()) {
          if (rule.forbidden.test(line)) {
            violations.push(`${rule.id} ${path.basename(file)}:${i + 1}`);
          }
        }
      });
    }
    return violations;
  }

  private static isComment(line: string): boolean {
    return line.startsWith('//') || line.startsWith('/*') || line.startsWith('*');
  }

  private static sourceFiles(dir: string): string[] {
    return readdirSync(dir)
      .filter((name) => name.endsWith('.ts'))
      .sort()
      .map((name) => path.join(dir, name));
  }
}
