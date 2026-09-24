import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { columnOf } from '../../../../src/workshop/m5/s15_compatibility/Column.js';
import * as step1Api from '../../../../src/workshop/m5/s15_compatibility/step1/BoxOfficeApi.js';
import * as step1Student from '../../../../src/workshop/m5/s15_compatibility/step1/StudentTicket.js';
import * as step2Api from '../../../../src/workshop/m5/s15_compatibility/step2/BoxOfficeApi.js';
import * as step2Student from '../../../../src/workshop/m5/s15_compatibility/step2/StudentTicket.js';
import * as step2Ticket from '../../../../src/workshop/m5/s15_compatibility/step2/Ticket.js';
import * as step3Api from '../../../../src/workshop/m5/s15_compatibility/step3/BoxOfficeApi.js';
import * as step3Student from '../../../../src/workshop/m5/s15_compatibility/step3/StudentTicket.js';
import * as step4Api from '../../../../src/workshop/m5/s15_compatibility/step4/BoxOfficeApi.js';
import * as step4Student from '../../../../src/workshop/m5/s15_compatibility/step4/StudentTicket.js';
import { workshopDir } from '../../support/paths.js';
import { compileErrors } from '../reflection.js';
import { run, type PluginApi } from './plugin/Plugin.js';

/** Wersje biblioteki, z którymi uruchamiamy zbudowaną wtyczkę (jak podmiana pakietu na serwerze). */
const API: Readonly<Record<string, PluginApi>> = {
  step1: { Money, StudentTicket: step1Student.StudentTicket, BoxOfficeApi: step1Api.BoxOfficeApi },
  step2: { Money, StudentTicket: step2Student.StudentTicket, BoxOfficeApi: step2Api.BoxOfficeApi },
  step3: { Money, StudentTicket: step3Student.StudentTicket, BoxOfficeApi: step3Api.BoxOfficeApi },
  step4: { Money, StudentTicket: step4Student.StudentTicket, BoxOfficeApi: step4Api.BoxOfficeApi },
};

/** Źródło wtyczki 1.x - sprawdzamy, czy przebudowa z nową wersją API się uda (zgodność źródłowa). */
const PLUGIN_SOURCE = `
  import { Money } from '../../../shared/Money.js';
  import { BoxOfficeApi } from './BoxOfficeApi.js';
  import { StudentTicket } from './StudentTicket.js';

  export function run(): string {
    const ticket = new StudentTicket('Amator', Money.of('25.00'));
    return \`\${ticket.price()}/\${new BoxOfficeApi().QUOTE(ticket)}\`;
  }
`;

function rebuildPlugin(step: string, quote: string): string[] {
  const probe = workshopDir('m5', 's15_compatibility', step, '__plugin__.ts');
  return compileErrors({ [probe]: PLUGIN_SOURCE.replace('QUOTE', quote) });
}

function hasDeclaredColumn(type: { readonly prototype: object }): boolean {
  return Object.getOwnPropertyNames(type.prototype)
    .some((name) => columnOf(Reflect.get(type.prototype, name)) !== undefined);
}

/**
 * Warstwy zgodności w TS: zbudowany JS wtyczki (odpowiednik binariów), źródło przebudowywane
 * z nową wersją API i "refleksja" po prototypach. Wersja bazowa "1.x" to step1.
 */
describe('S15SolutionTest', () => {
  it('pullUpIsBinaryCompatibleForCallers', () => {
    expect(run(API.step1!)).toBe('18.75/18.75');
    // studentTicket.price() znalezione w łańcuchu prototypów (w Ticket)
    expect(run(API.step2!)).toBe('18.75/18.75');
  });

  it('generalizedParameterBreaksOldBinary', () => {
    expect(() => run(API.step3!)).toThrow(TypeError);
    expect(() => run(API.step3!)).toThrow(/quoteStudent/);
  });

  // Java: generalizedParameterIsSourceCompatible. W TS nazwa jest całym "deskryptorem", więc
  // stare źródło wtyczki nie przebuduje się ze step3 (TS2339) - build to wykryje; działa dopiero
  // źródło przepisane na quote(...).
  it('generalizedParameterIsSourceCompatibleOnlyAfterRename', () => {
    expect(rebuildPlugin('step1', 'quoteStudent')).toEqual([]);
    expect(rebuildPlugin('step3', 'quoteStudent')).toEqual(['TS2339']);
    expect(rebuildPlugin('step3', 'quote')).toEqual([]);
  });

  it('delegatingOverloadRestoresBinaryCompatibility', () => {
    expect(run(API.step4!)).toBe('18.75/18.75');
    expect(rebuildPlugin('step4', 'quoteStudent')).toEqual([]);
  });

  it('pullUpHidesAnnotatedMethodFromDeclaredMethodsLookup', () => {
    expect(hasDeclaredColumn(step1Student.StudentTicket)).toBe(true);
    // pułapka: eksporter oparty tylko na prototypie klasy runtime nie znalazłby kolumny po Pull Up
    expect(hasDeclaredColumn(step2Student.StudentTicket)).toBe(false);
    expect(hasDeclaredColumn(step2Ticket.Ticket)).toBe(true);
  });
});
