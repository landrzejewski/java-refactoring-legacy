import ts from 'typescript-api';

import type { ProjectFile } from '../SampleProject.js';

/**
 * Krok 1: wyszukiwanie przez AST (parser TypeScript). Parser widzi wywołania metod,
 * a nie linie tekstu: komentarz przestaje być trafieniem, wywołanie na trzech liniach jest
 * znalezione. Przepisanie nadal tekstowe (bez zmian od startu).
 */
export class BookCallCodemod {
  /** Źródła projektu (API) - samo parsowanie składni ich nie potrzebuje. */
  constructor(_projectSources: readonly ProjectFile[]) {}

  findLines(source: string): number[] {
    const unit = ts.createSourceFile('Source.ts', source, ts.ScriptTarget.ES2023, true);
    const lines: number[] = [];
    const visit = (node: ts.Node): void => {
      if (ts.isCallExpression(node) && BookCallCodemod.looksLikeOldBook(node)) {
        lines.push(unit.getLineAndCharacterOfPosition(node.getStart(unit)).line + 1);
      }
      ts.forEachChild(node, visit);
    };
    visit(unit);
    return lines;
  }

  rewrite(source: string): string {
    return source
      .replace(', true, true)', ', Channel.WEB, Glasses.OWN)')
      .replace(', true, false)', ', Channel.WEB, Glasses.RENTED)')
      .replace(', false, true)', ', Channel.BOX_OFFICE, Glasses.OWN)')
      .replace(', false, false)', ', Channel.BOX_OFFICE, Glasses.RENTED)')
      .replace("import { BookingService } from '../cinema/BookingService.js';",
        "import { BookingService } from '../cinema/BookingService.js';\n"
        + "import { Channel } from '../cinema/Channel.js';\n"
        + "import { Glasses } from '../cinema/Glasses.js';");
  }

  /** Składnia: metoda o nazwie book z sześcioma argumentami. Typu odbiorcy nie znamy. */
  private static looksLikeOldBook(call: ts.CallExpression): boolean {
    return ts.isPropertyAccessExpression(call.expression)
      && call.expression.name.text === 'book'
      && call.arguments.length === 6;
  }
}
