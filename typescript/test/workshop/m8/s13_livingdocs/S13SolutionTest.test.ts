import { existsSync, readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m8/s13_livingdocs/start/Routing.js';
import * as step1 from '../../../../src/workshop/m8/s13_livingdocs/step1/Routing.js';
import * as step1doc from '../../../../src/workshop/m8/s13_livingdocs/step1/RoutingDoc.js';
import * as step2 from '../../../../src/workshop/m8/s13_livingdocs/step2/Routing.js';
import * as step2doc from '../../../../src/workshop/m8/s13_livingdocs/step2/RoutingDoc.js';
import { workshopDir } from '../../support/paths.js';

function read(variant: string): string {
  const doc = workshopDir('m8', 's13_livingdocs', variant, 'ROUTING.md');
  expect(existsSync(doc), 'brak ' + doc).toBe(true);
  return readFileSync(doc, 'utf8');
}

/** Porównuje dwie pierwsze kolumny tabeli z dokumentu z routingiem z kodu. */
function drift(markdown: string, code: ReadonlyMap<string, string>): string[] {
  const documented = new Map<string, string>();
  for (const line of markdown.split(/\r?\n/)) {
    const cells = line.split('|');
    if (line.startsWith('|') && cells.length > 2 && cells[1]!.trim() !== ''
      && cells[1]!.trim() !== 'Operacja' && !cells[1]!.trim().startsWith('---')) {
      documented.set(cells[1]!.trim(), cells[2]!.trim());
    }
  }
  const problems: string[] = [];
  code.forEach((target, operation) => {
    const inDoc = documented.get(operation);
    if (inDoc === undefined) {
      problems.push(operation + ': brak w dokumencie');
    } else if (inDoc !== target) {
      problems.push(operation + ': dokument mowi ' + inDoc + ', kod mowi ' + target);
    }
  });
  return problems;
}

/** Dokumentacja żywa: ROUTING.md musi zgadzać się z kodem. */
describe('S13SolutionTest', () => {
  it('startDocumentationHasDriftedFromCode', () => {
    const code = new Map(start.Routing.routes().map((route) => [route.operation, route.target]));
    expect(drift(read('start'), code))
      .toEqual(['report: dokument mowi legacy, kod mowi new', 'cancel: brak w dokumencie']);
  });

  it('step1DocumentIsGeneratedFromCode', () => {
    expect(read('step1'), 'ROUTING.md nieaktualny - uruchom RoutingDoc.main()')
      .toBe(step1doc.RoutingDoc.render(step1.Routing.routes()));
  });

  it('step2DocumentIsGeneratedFromCode', () => {
    expect(read('step2'), 'ROUTING.md nieaktualny - uruchom RoutingDoc.main()')
      .toBe(step2doc.RoutingDoc.render(step2.Routing.routes()));
  });

  it('step2EveryTransitionalRouteHasOwnerAndRemovalCriterion', () => {
    for (const route of step2.Routing.routes()) {
      expect(route.owner.trim(), route.operation).not.toBe('');
      if (route.target === 'legacy') {
        expect(route.removeWhen, 'trasa do legacy bez kryterium usunięcia: ' + JSON.stringify(route)).not.toBe('-');
      }
    }
  });

  it('documentLocationFollowsThePackage', () => {
    expect(step2doc.RoutingDoc.location()).toBe(workshopDir('m8', 's13_livingdocs', 'step2', 'ROUTING.md'));
  });
});
