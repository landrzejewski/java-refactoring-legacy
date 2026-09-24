import { writeFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';

import { type Route, Routing } from './Routing.js';

/**
 * Krok 2: dokument pokazuje też architekturę przejściową - właściciela i kryterium usunięcia
 * każdej trasy. Informacja operacyjna jest żywa, uzasadnienie decyzji zostaje w ADR (historia).
 */
export class RoutingDoc {
  private constructor() {}

  static render(routes: readonly Route[]): string {
    let md = '# Routing CineLegacy\n\n'
      + 'Plik generowany z Routing.routes() przez RoutingDoc - nie edytuj ręcznie.\n\n'
      + '| Operacja | Obsługuje | Właściciel | Usunąć, gdy |\n'
      + '| --- | --- | --- | --- |\n';
    for (const route of routes) {
      md += '| ' + route.operation + ' | ' + route.target + ' | ' + route.owner
        + ' | ' + route.removeWhen + ' |' + '\n';
    }
    return md;
  }

  /** Ścieżka dokumentu obok kodu - liczona z adresu modułu, więc działa też po "jump" do start. */
  static location(): string {
    return fileURLToPath(new URL('./ROUTING.md', import.meta.url));
  }

  /** Regeneracja dokumentu - zapisuje ROUTING.md obok modułu. */
  static main(): void {
    writeFileSync(RoutingDoc.location(), RoutingDoc.render(Routing.routes()));
  }
}
