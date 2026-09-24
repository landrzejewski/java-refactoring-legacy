import { existsSync, readFileSync } from 'node:fs';
import path from 'node:path';

import ts from 'typescript-api';
import { describe, expect, it } from 'vitest';

import { SampleProject } from '../../../../src/workshop/m8/s09_codemod/SampleProject.js';
import * as start from '../../../../src/workshop/m8/s09_codemod/start/BookCallCodemod.js';
import * as step1 from '../../../../src/workshop/m8/s09_codemod/step1/BookCallCodemod.js';
import * as step2 from '../../../../src/workshop/m8/s09_codemod/step2/BookCallCodemod.js';
import * as step3 from '../../../../src/workshop/m8/s09_codemod/step3/BookCallCodemod.js';

const DESK = SampleProject.TICKET_DESK;

const MIGRATED = `// desk/TicketDesk.ts - klient do migracji

import { BookingService } from '../cinema/BookingService.js';
import { HotelService } from '../hotel/HotelService.js';
import { Channel } from '../cinema/Channel.js';
import { Glasses } from '../cinema/Glasses.js';

export class TicketDesk {
  private readonly bookings = new BookingService();
  private readonly hotels = new HotelService();

  online(email: string, seats: string[], types: string[]): string {
    return this.bookings.book('S1', email, seats, types, Channel.WEB, Glasses.RENTED);
  }

  boxOffice(seats: string[], types: string[], own: boolean): string {
    // stary przyklad: bookings.book('S1', 'x', seats, types, false, true)
    return this.bookings.book('S2', 'kasa@kino.pl',
      seats, types,
      Channel.BOX_OFFICE, own ? Glasses.OWN : Glasses.RENTED);
  }

  stay(email: string, rooms: string[], guests: string[]): string {
    return this.hotels.book('H1', email, rooms, guests, true, true);
  }
}
`;

const ROOT = '/project';
const OPTIONS: ts.CompilerOptions = {
  target: ts.ScriptTarget.ES2023,
  module: ts.ModuleKind.ESNext,
  moduleResolution: ts.ModuleResolutionKind.Bundler,
  lib: ['lib.es2023.d.ts'],
  types: [],
  strict: true,
  noEmit: true,
};

/**
 * Analiza próbki razem z API (strict + ostrzeżenia o przestarzałym API z usługi językowej);
 * zwraca błędy i ostrzeżenia, np. "WARNING TS6385".
 */
function problems(code: string): string[] {
  const files = new Map<string, string>();
  SampleProject.API.forEach((file) => files.set(path.posix.join(ROOT, file.path), file.text));
  files.set(path.posix.join(ROOT, 'desk/TicketDesk.ts'), code);
  const libraryDirectory = path.dirname(ts.getDefaultLibFilePath(OPTIONS));
  const read = (fileName: string): string | undefined =>
    files.get(fileName) ?? (path.dirname(fileName) === libraryDirectory && existsSync(fileName) ? readFileSync(fileName, 'utf8') : undefined);
  const service = ts.createLanguageService({
    getCompilationSettings: () => OPTIONS,
    getScriptFileNames: () => [...files.keys()],
    getScriptVersion: () => '1',
    getScriptSnapshot: (fileName) => {
      const text = read(fileName);
      return text === undefined ? undefined : ts.ScriptSnapshot.fromString(text);
    },
    getCurrentDirectory: () => ROOT,
    getDefaultLibFileName: (options) => path.join(libraryDirectory, ts.getDefaultLibFileName(options)),
    fileExists: (fileName) => read(fileName) !== undefined,
    readFile: read,
  });
  const result: string[] = [];
  for (const fileName of files.keys()) {
    for (const d of [...service.getSyntacticDiagnostics(fileName), ...service.getSemanticDiagnostics(fileName)]) {
      result.push('ERROR TS' + d.code);
    }
    for (const d of service.getSuggestionDiagnostics(fileName)) {
      if (d.reportsDeprecated) {
        result.push('WARNING TS' + d.code);
      }
    }
  }
  return result;
}

/** Codemod na próbce kodu: co znajduje i jak przepisuje każda wersja; wynik musi się kompilować. */
describe('S09SolutionTest', () => {
  it('startRegexHitsCommentAndHotelButMissesMultilineCall', () => {
    const codemod = new start.BookCallCodemod(SampleProject.API);
    expect(codemod.findLines(DESK)).toEqual([11, 15, 22]);
    const rewritten = codemod.rewrite(DESK);
    expect(rewritten.includes("// stary przyklad: bookings.book('S1', 'x', seats, types, "
      + 'Channel.BOX_OFFICE, Glasses.OWN)'), 'regex przepisał komentarz').toBe(true);
    expect(rewritten.includes('false, own);'), 'wywołanie na kilku liniach zostało nietknięte').toBe(true);
  });

  it('step1AstSearchFindsRealCallsIncludingMultilineOne', () => {
    const codemod = new step1.BookCallCodemod(SampleProject.API);
    expect(codemod.findLines(DESK), '22 = HotelService: składnia nie zna typów').toEqual([11, 16, 22]);
  });

  it('step2SyntacticRewriteBreaksTheHotelCallAndIsNotIdempotent', () => {
    const codemod = new step2.BookCallCodemod(SampleProject.API);
    const once = codemod.rewrite(DESK);
    expect(once).toContain('Channel.BOX_OFFICE, own ? Glasses.OWN : Glasses.RENTED);');
    expect(once).toContain("hotels.book('H1', email, rooms, guests, Channel.WEB, Glasses.OWN)");
    expect(problems(once), 'HotelService nie ma book(..., Channel, Glasses)').not.toEqual([]);
    expect(codemod.rewrite(once), 'drugie uruchomienie psuje już zmigrowane wywołania').not.toBe(once);
  });

  it('step3TypeAwareRewriteMigratesOnlyTheDeprecatedApi', () => {
    const codemod = new step3.BookCallCodemod(SampleProject.API);
    expect(codemod.findLines(DESK)).toEqual([11, 16]);
    expect(codemod.rewrite(DESK)).toBe(MIGRATED);
  });

  it('step3ResultCompilesWithoutWarningsAndSecondRunChangesNothing', () => {
    const codemod = new step3.BookCallCodemod(SampleProject.API);
    expect(problems(MIGRATED)).toEqual([]);
    expect(codemod.rewrite(MIGRATED)).toBe(MIGRATED);
    expect(codemod.findLines(MIGRATED)).toEqual([]);
  });

  it('originalSampleCompilesButUsesDeprecatedApi', () => {
    expect(problems(DESK)).toEqual(['WARNING TS6385', 'WARNING TS6385']);
  });
}, 60_000);
