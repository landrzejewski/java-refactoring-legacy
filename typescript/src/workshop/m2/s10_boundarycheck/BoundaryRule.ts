import { readdirSync, readFileSync } from 'node:fs';
import { basename, join } from 'node:path';

/**
 * Narzędzie sceny: najprostszy test architektury bez bibliotek. Skanuje pliki .ts
 * w katalogu (np. domain) i zgłasza importy, których ścieżka zawiera zakazany fragment.
 *
 * Ograniczenia (świadome): widzi tylko instrukcje import/export ... from - dynamiczne
 * import() przejdzie. Silniejsze bramki: dependency-cruiser, eslint (no-restricted-imports,
 * eslint-plugin-boundaries), osobne pakiety workspace, project references w tsconfig, reguły w CI.
 */
export class BoundaryRule {
  private static readonly IMPORT = /^\s*(?:import|export)\s(?:[^;]*?\sfrom\s*)?['"]([^'"]+)['"]/gm;

  private readonly forbiddenFragments: readonly string[];

  constructor(...forbiddenFragments: string[]) {
    this.forbiddenFragments = Object.freeze([...forbiddenFragments]);
  }

  /** Naruszenia w formacie "Plik.ts: ścieżka/importu.js", posortowane po pliku. */
  violations(sourceDir: string): string[] {
    return BoundaryRule.walk(sourceDir)
      .filter((path) => path.endsWith('.ts'))
      .sort()
      .flatMap((path) => BoundaryRule.importsOf(path)
        .filter((imported) => this.isForbidden(imported))
        .map((imported) => `${basename(path)}: ${imported}`));
  }

  private isForbidden(imported: string): boolean {
    return this.forbiddenFragments.some((fragment) => imported.includes(fragment));
  }

  private static importsOf(file: string): string[] {
    return [...readFileSync(file, 'utf8').matchAll(BoundaryRule.IMPORT)].map((m) => m[1] ?? '');
  }

  private static walk(dir: string): string[] {
    return readdirSync(dir, { withFileTypes: true }).flatMap((entry) => {
      const path = join(dir, entry.name);
      return entry.isDirectory() ? BoundaryRule.walk(path) : [path];
    });
  }
}
