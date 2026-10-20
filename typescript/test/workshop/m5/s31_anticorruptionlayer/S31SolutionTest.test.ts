import { existsSync, readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDate } from '../../../../src/workshop/shared/time.js';
import { DistributorFeed } from '../../../../src/workshop/m5/s31_anticorruptionlayer/DistributorFeed.js';
import { DistributorRecord } from '../../../../src/workshop/m5/s31_anticorruptionlayer/DistributorRecord.js';
import * as aclModule from '../../../../src/workshop/m5/s31_anticorruptionlayer/step3/DistributorAcl.js';
import { FilmLicense } from '../../../../src/workshop/m5/s31_anticorruptionlayer/step3/FilmLicense.js';
import { SettlementService } from '../../../../src/workshop/m5/s31_anticorruptionlayer/step3/SettlementService.js';
import { workshopDir } from '../../support/paths.js';

const { DistributorAcl } = aclModule;
const STEP3 = (file: string) => workshopDir('m5', 's31_anticorruptionlayer', 'step3', file);

/** Typy dystrybutora nie przekraczają ACL; licencja w języku kina pilnuje swoich niezmienników. */
describe('S31SolutionTest', () => {
  // TypeScript nie ma refleksji typów: zamiast sygnatur klasy sprawdzamy importy modułu -
  // serwis domeny nie importuje niczego z katalogu sceny (DistributorFeed, DistributorRecord).
  it('domainServiceUsesNoLegacyType', () => {
    const source = readFileSync(STEP3('SettlementService.ts'), 'utf8');
    expect(source).not.toMatch(/from '\.\.\/[^./][^']*'/);
    expect(source).not.toMatch(/\bDistributor(Feed|Record)\b/);
  });

  // Brak widoczności pakietowej: translator to klasa modułu DistributorAcl bez eksportu.
  it('translatorIsAnImplementationDetailOfTheAcl', () => {
    expect(existsSync(STEP3('DistributorTranslator.ts'))).toBe(false);
    expect(Object.keys(aclModule)).toEqual(['DistributorAcl']);
    expect(readFileSync(STEP3('DistributorAcl.ts'), 'utf8')).toMatch(/^class DistributorTranslator\b/m);
  });

  it('aclTranslatesLegacyConventions', () => {
    const acl = new DistributorAcl(new DistributorFeed([
      new DistributorRecord('KRL', 'KRAINA LODU', 'S', '20261015', '20261115', '30000', ' 40 %'),
    ]));
    expect(acl.find('KRL')).toEqual(new FilmLicense('KRL', 'Kraina lodu', false, LocalDate.of(2026, 10, 15),
      LocalDate.of(2026, 11, 15), 40, Money.of('300.00')));
    expect(acl.find('XYZ'), 'null z systemu dystrybutora nie wychodzi poza ACL').toBeUndefined();
  });

  it('aclRejectsMalformedLegacyDataAtTheBoundary', () => {
    const acl = new DistributorAcl(new DistributorFeed([
      new DistributorRecord('DIU', 'DIUNA', 'A', '2026-10-01', '20261031', '50000', '45%'),
    ]));
    expect(() => acl.find('DIU')).toThrow(IllegalArgumentError);
    expect(() => acl.find('DIU')).toThrow('invalid legacy date: 2026-10-01');
  });

  it('licenseProtectsItsInvariants', () => {
    const day = LocalDate.of(2026, 10, 20);
    expect(() => new FilmLicense('DIU', 'Diuna', true, day, day.plusDays(-1), 45, Money.ZERO))
      .toThrow(IllegalArgumentError);
    expect(() => new FilmLicense('DIU', 'Diuna', true, day, day, 120, Money.ZERO)).toThrow(IllegalArgumentError);
  });

  it('serviceWorksWithAnyLicenseSource', () => {
    const day = LocalDate.of(2026, 10, 20);
    const festival = new FilmLicense('FST', 'Festiwal', true, day, day, 10, Money.of('50.00'));
    const service = new SettlementService({ find: () => festival });
    expect(service.canScreen('FST', day)).toBe(true);
    expect(service.settle('FST', day, Money.of('1000.00'))).toBe('ROZLICZENIE Festiwal 2026-10-20: przychod 1000.00, '
      + 'udzial 10%, minimum 50.00, dla dystrybutora 100.00');
  });
});
