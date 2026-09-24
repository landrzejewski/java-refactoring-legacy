import ts from 'typescript-api';

import type { ProjectFile } from '../SampleProject.js';

interface Edit {
  readonly start: number;
  readonly end: number;
  readonly text: string;
}

interface Parsed {
  readonly unit: ts.SourceFile;
  readonly calls: readonly ts.CallExpression[];
}

/**
 * Krok 2: przepisanie na podstawie pozycji z AST - podmieniamy dokładnie dwa ostatnie argumenty
 * (literał -> stała enum, wyrażenie -> operator warunkowy) i dopisujemy importy. Formatowanie
 * reszty pliku zostaje. Nadal tylko składnia: HotelService.book też zostanie "zmigrowany".
 */
export class BookCallCodemod {
  /** Źródła projektu (API) - samo parsowanie składni ich nie potrzebuje. */
  constructor(_projectSources: readonly ProjectFile[]) {}

  findLines(source: string): number[] {
    const parsed = BookCallCodemod.parse(source);
    return parsed.calls.map((call) => parsed.unit.getLineAndCharacterOfPosition(call.getStart(parsed.unit)).line + 1);
  }

  rewrite(source: string): string {
    const parsed = BookCallCodemod.parse(source);
    const edits: Edit[] = [];
    for (const call of parsed.calls) {
      const args = call.arguments;
      edits.push(BookCallCodemod.replace(parsed.unit, args[4]!, 'Channel.WEB', 'Channel.BOX_OFFICE'));
      edits.push(BookCallCodemod.replace(parsed.unit, args[5]!, 'Glasses.OWN', 'Glasses.RENTED'));
    }
    if (edits.length === 0) {
      return source;
    }
    edits.push(BookCallCodemod.imports(parsed.unit, source));
    let result = source;
    for (const edit of [...edits].sort((a, b) => b.start - a.start)) {
      result = result.substring(0, edit.start) + edit.text + result.substring(edit.end);
    }
    return result;
  }

  private static replace(unit: ts.SourceFile, arg: ts.Expression, ifTrue: string, ifFalse: string): Edit {
    const start = arg.getStart(unit);
    const end = arg.getEnd();
    if (arg.kind === ts.SyntaxKind.TrueKeyword || arg.kind === ts.SyntaxKind.FalseKeyword) {
      return { start, end, text: arg.kind === ts.SyntaxKind.TrueKeyword ? ifTrue : ifFalse };
    }
    const text = unit.text.substring(start, end);
    const simple = ts.isIdentifier(arg) || ts.isPropertyAccessExpression(arg) || ts.isCallExpression(arg);
    return { start, end, text: (simple ? text : '(' + text + ')') + ' ? ' + ifTrue + ' : ' + ifFalse };
  }

  private static imports(unit: ts.SourceFile, source: string): Edit {
    let missing = '';
    for (const type of ['Channel', 'Glasses']) {
      const line = `import { ${type} } from '../cinema/${type}.js';`;
      if (!source.includes(line)) {
        missing += '\n' + line;
      }
    }
    const imports = unit.statements.filter(ts.isImportDeclaration);
    const at = imports[imports.length - 1]!.getEnd();
    return { start: at, end: at, text: missing };
  }

  private static parse(source: string): Parsed {
    const unit = ts.createSourceFile('Source.ts', source, ts.ScriptTarget.ES2023, true);
    const calls: ts.CallExpression[] = [];
    const visit = (node: ts.Node): void => {
      if (ts.isCallExpression(node) && BookCallCodemod.looksLikeOldBook(node)) {
        calls.push(node);
      }
      ts.forEachChild(node, visit);
    };
    visit(unit);
    return { unit, calls };
  }

  /** Składnia: metoda o nazwie book z sześcioma argumentami. Typu odbiorcy nie znamy. */
  private static looksLikeOldBook(call: ts.CallExpression): boolean {
    return ts.isPropertyAccessExpression(call.expression)
      && call.expression.name.text === 'book'
      && call.arguments.length === 6;
  }
}
