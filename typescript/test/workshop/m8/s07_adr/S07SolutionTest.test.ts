import { existsSync, readFileSync, statSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { ArchitectureRules, Rule } from '../../../../src/workshop/m8/s07_adr/ArchitectureRules.js';
import { workshopDir } from '../../support/paths.js';

function variant(name: string): string {
  const dir = workshopDir('m8', 's07_adr', name);
  expect(existsSync(dir) && statSync(dir).isDirectory(), 'brak katalogu ' + dir).toBe(true);
  return dir;
}

function ruleIds(violations: readonly string[]): string[] {
  return [...new Set(violations.map((v) => v.substring(0, v.indexOf(' '))))].sort();
}

/** Wykonywalny model decyzji: start narusza ADR-0007, kolejne kroki doprowadzają do zgodności. */
describe('S07SolutionTest', () => {
  it('startViolatesBothRules', () => {
    const violations = ArchitectureRules.violations(variant('start'));
    expect(violations, violations.join(', ')).toContain('ADR-0007/R1 TicketPricing.ts:1');
    expect(ruleIds(violations)).toEqual(['ADR-0007/R1', 'ADR-0007/R2']);
  });

  it('step1RemovesDependencyOnNotification', () => {
    expect(ruleIds(ArchitectureRules.violations(variant('step1')))).toEqual(['ADR-0007/R2']);
  });

  it('step2CompliesWithTheDecision', () => {
    expect(ArchitectureRules.violations(variant('step2'))).toEqual([]);
  });

  it('adrDocumentsEveryExecutableRule', () => {
    const adr = readFileSync(workshopDir('m8', 's07_adr', 'ADR-0007-cennik-jako-czysty-modul.md'), 'utf8');
    expect(adr).toContain('**Status:** Zaakceptowana');
    for (const rule of Rule.values()) {
      const shortId = rule.id.substring(rule.id.indexOf('/') + 1);
      expect(adr.includes('**' + shortId + '.**'), 'ADR nie opisuje reguły ' + rule.id).toBe(true);
    }
  });
});
