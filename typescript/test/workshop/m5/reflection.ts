import { readFileSync } from 'node:fs';
import ts from 'typescript-api';

import { workshopDir } from '../support/paths.js';

// Pomocnicy testów modułu 5 - odpowiedniki refleksji Javy (getDeclaredMethods, Modifier).
// Zachowanie w czasie działania sprawdzamy na prototypach, a modyfikatory, które TypeScript
// usuwa przy kompilacji (abstract, private, override...), czytamy z drzewa składni źródła sceny.

// Każda klasa (także abstrakcyjna, z konstruktorem protected) ma prototyp.
type AnyClass = { readonly prototype: object };

// Odpowiednik type.getDeclaredMethods() zawierającego metodę o danej nazwie.
export function declares(type: AnyClass, name: string): boolean {
  return Object.getOwnPropertyNames(type.prototype).includes(name);
}

// Odpowiednik type.getSuperclass(): klasa bazowa albo undefined dla klasy bez extends
// (odpowiednik Object.class - konstruktor bez nadklasy dziedziczy po Function.prototype).
export function superclassOf(type: AnyClass): unknown {
  const parent: unknown = Object.getPrototypeOf(type);
  return parent === Function.prototype ? undefined : parent;
}

// Odpowiednik type.getMethod(name).getDeclaringClass(): klasa, której prototyp (w łańcuchu
// dziedziczenia) deklaruje metodę; undefined odpowiada NoSuchMethodException.
export function declaringClassOf(type: AnyClass, name: string): unknown {
  for (let proto: object | null = type.prototype; proto !== null && proto !== Object.prototype; proto = Object.getPrototypeOf(proto)) {
    if (Object.getOwnPropertyNames(proto).includes(name)) {
      return (proto as { constructor: unknown }).constructor;
    }
  }
  return undefined;
}

// Źródło pliku sceny jako drzewo składni, np. sourceOf('s01_pullupmethod', 'step3', 'Ticket.ts').
export function sourceOf(...parts: string[]): ts.SourceFile {
  const file = workshopDir('m5', ...parts);
  return ts.createSourceFile(file, readFileSync(file, 'utf8'), ts.ScriptTarget.Latest, true);
}

export function classDeclaration(source: ts.SourceFile, className: string): ts.ClassDeclaration {
  const found = source.statements.find(
    (statement): statement is ts.ClassDeclaration =>
      ts.isClassDeclaration(statement) && statement.name?.text === className,
  );
  if (found === undefined) {
    throw new Error(`no class ${className} in ${source.fileName}`);
  }
  return found;
}

// Modyfikatory klasy, np. ['export', 'abstract'] - odpowiednik Modifier.isAbstract(type.getModifiers()).
export function classModifiersOf(source: ts.SourceFile, className: string): string[] {
  return (ts.getModifiers(classDeclaration(source, className)) ?? []).map((modifier) => modifier.getText(source));
}

// Składowa klasy o danej nazwie (pole prywatne ES ma nazwę z "#", np. '#seat') albo undefined.
export function memberOf(source: ts.SourceFile, className: string, memberName: string): ts.ClassElement | undefined {
  return classDeclaration(source, className).members.find(
    (candidate) => candidate.name !== undefined && candidate.name.getText(source) === memberName,
  );
}

// Odpowiednik type.getDeclaredField(name) != null.
export function declaresMember(source: ts.SourceFile, className: string, memberName: string): boolean {
  return memberOf(source, className, memberName) !== undefined;
}

function requireMember(source: ts.SourceFile, className: string, memberName: string): ts.ClassElement {
  const member = memberOf(source, className, memberName);
  if (member === undefined) {
    throw new Error(`no member ${className}.${memberName}`);
  }
  return member;
}

// Modyfikatory składowej klasy (metody, pola, akcesora), np. ['abstract'] albo ['private', 'readonly'].
export function modifiersOf(source: ts.SourceFile, className: string, memberName: string): string[] {
  const member = requireMember(source, className, memberName);
  return (ts.getModifiers(member as ts.HasModifiers) ?? []).map((modifier) => modifier.getText(source));
}

// Zadeklarowany typ pola (tekst adnotacji), odpowiednik Field.getType().
export function fieldTypeOf(source: ts.SourceFile, className: string, fieldName: string): string | undefined {
  const member = requireMember(source, className, fieldName);
  return ts.isPropertyDeclaration(member) ? member.type?.getText(source) : undefined;
}

// Nazwy pól zadeklarowanych w klasie (także #prywatnych) - odpowiednik getDeclaredFields().
export function declaredFields(source: ts.SourceFile, className: string): string[] {
  return classDeclaration(source, className).members
    .filter(ts.isPropertyDeclaration)
    .map((field) => field.name.getText(source));
}

// Kompiluje w pamięci pliki wirtualne (ścieżka bezwzględna -> treść) razem z prawdziwymi plikami,
// które importują (np. kod sceny), i zwraca kody błędów, np. ['TS2416']. Odpowiednik javac
// uruchamianego z testu - pakiet `typescript-api` (TypeScript 5.9 z API dla JS).
export function compileErrors(virtualFiles: Readonly<Record<string, string>>): string[] {
  const options: ts.CompilerOptions = {
    target: ts.ScriptTarget.ES2023,
    lib: ['lib.es2023.d.ts'],
    module: ts.ModuleKind.NodeNext,
    moduleResolution: ts.ModuleResolutionKind.NodeNext,
    types: [],
    strict: true,
    exactOptionalPropertyTypes: true,
    noUncheckedIndexedAccess: true,
    noImplicitOverride: true,
    skipLibCheck: true,
    noEmit: true,
  };
  const host = ts.createCompilerHost(options);
  const virtual = new Map(Object.entries(virtualFiles));
  const { getSourceFile, readFile, fileExists } = host;
  host.getSourceFile = (fileName, languageVersion, ...rest) => {
    const text = virtual.get(fileName);
    return text === undefined
      ? getSourceFile.call(host, fileName, languageVersion, ...rest)
      : ts.createSourceFile(fileName, text, languageVersion, true);
  };
  host.readFile = (fileName) => virtual.get(fileName) ?? readFile.call(host, fileName);
  host.fileExists = (fileName) => virtual.has(fileName) || fileExists.call(host, fileName);
  const program = ts.createProgram({ rootNames: [...virtual.keys()], options, host });
  return ts.getPreEmitDiagnostics(program).map((diagnostic) => `TS${diagnostic.code}`);
}
