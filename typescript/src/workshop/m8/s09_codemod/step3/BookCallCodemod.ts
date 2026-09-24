import { existsSync, readFileSync } from 'node:fs';
import path from 'node:path';

import ts from 'typescript-api';

import type { ProjectFile } from '../SampleProject.js';

interface Edit {
  readonly start: number;
  readonly end: number;
  readonly text: string;
}

interface Analyzed {
  readonly unit: ts.SourceFile;
  readonly calls: readonly ts.CallExpression[];
}

/**
 * Krok 3: dopasowanie po typach, nie po nazwie. Kod jest analizowany razem ze źródłami projektu
 * (Program + TypeChecker), a wywołanie trafia do migracji tylko wtedy, gdy kompilator rozwiązał
 * je do przestarzałej sygnatury BookingService.book z cinema/BookingService.ts. HotelService
 * zostaje w spokoju, a ponowne uruchomienie na zmigrowanym kodzie niczego nie zmienia (idempotencja).
 */
export class BookCallCodemod {
  private static readonly ROOT = '/project';
  private static readonly OLD_API_FILE = 'cinema/BookingService.ts';
  private static readonly OLD_API_OWNER = 'BookingService';
  // Migrowany plik leży obok klientów, więc importy '../cinema/...' się rozwiązują.
  private static readonly SOURCE_FILE = 'desk/Source.ts';
  private static readonly OPTIONS: ts.CompilerOptions = {
    target: ts.ScriptTarget.ES2023,
    module: ts.ModuleKind.ESNext,
    moduleResolution: ts.ModuleResolutionKind.Bundler,
    lib: ['lib.es2023.d.ts'],
    types: [],
    strict: true,
    noEmit: true,
  };

  private readonly projectSources: readonly ProjectFile[];

  constructor(projectSources: readonly ProjectFile[]) {
    this.projectSources = Object.freeze([...projectSources]);
  }

  findLines(source: string): number[] {
    const analyzed = this.analyze(source);
    return analyzed.calls.map((call) => analyzed.unit.getLineAndCharacterOfPosition(call.getStart(analyzed.unit)).line + 1);
  }

  rewrite(source: string): string {
    const parsed = this.analyze(source);
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

  private analyze(source: string): Analyzed {
    const files = new Map<string, string>();
    this.projectSources.forEach((file) => files.set(BookCallCodemod.absolute(file.path), file.text));
    const sourcePath = BookCallCodemod.absolute(BookCallCodemod.SOURCE_FILE);
    files.set(sourcePath, source);
    const program = ts.createProgram({
      rootNames: [...files.keys()],
      options: BookCallCodemod.OPTIONS,
      host: BookCallCodemod.host(files),
    });
    const checker = program.getTypeChecker();
    const unit = program.getSourceFile(sourcePath)!;
    const calls: ts.CallExpression[] = [];
    const visit = (node: ts.Node): void => {
      if (ts.isCallExpression(node) && BookCallCodemod.isOldBook(checker.getResolvedSignature(node))) {
        calls.push(node);
      }
      ts.forEachChild(node, visit);
    };
    visit(unit);
    return { unit, calls };
  }

  /** Typy: przestarzała sygnatura book zadeklarowana w klasie BookingService z cinema/BookingService.ts. */
  private static isOldBook(signature: ts.Signature | undefined): boolean {
    const declaration = signature?.getDeclaration();
    if (declaration === undefined || !ts.isMethodDeclaration(declaration)
      || !ts.isIdentifier(declaration.name) || declaration.name.text !== 'book') {
      return false;
    }
    const owner = declaration.parent;
    return signature!.getJsDocTags().some((tag) => tag.name === 'deprecated')
      && ts.isClassDeclaration(owner)
      && owner.name?.text === BookCallCodemod.OLD_API_OWNER
      && owner.getSourceFile().fileName === BookCallCodemod.absolute(BookCallCodemod.OLD_API_FILE);
  }

  private static absolute(relative: string): string {
    return path.posix.join(BookCallCodemod.ROOT, relative);
  }

  /** Host w pamięci: pliki projektu z mapy, pliki lib.*.d.ts z pakietu typescript-api. */
  private static host(files: ReadonlyMap<string, string>): ts.CompilerHost {
    const libraryDirectory = path.dirname(ts.getDefaultLibFilePath(BookCallCodemod.OPTIONS));
    const read = (fileName: string): string | undefined =>
      files.get(fileName) ?? (path.dirname(fileName) === libraryDirectory && existsSync(fileName) ? readFileSync(fileName, 'utf8') : undefined);
    return {
      getSourceFile: (fileName, languageVersion) => {
        const text = read(fileName);
        return text === undefined ? undefined : ts.createSourceFile(fileName, text, languageVersion, true);
      },
      getDefaultLibFileName: (options) => path.join(libraryDirectory, ts.getDefaultLibFileName(options)),
      getDefaultLibLocation: () => libraryDirectory,
      writeFile: () => undefined,
      getCurrentDirectory: () => BookCallCodemod.ROOT,
      getDirectories: () => [],
      fileExists: (fileName) => files.has(fileName)
        || (path.dirname(fileName) === libraryDirectory && ts.sys.fileExists(fileName)),
      readFile: read,
      getCanonicalFileName: (fileName) => fileName,
      useCaseSensitiveFileNames: () => true,
      getNewLine: () => '\n',
    };
  }
}
