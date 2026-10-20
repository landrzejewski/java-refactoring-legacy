import { IllegalArgumentError } from '../../../../shared/errors.js';
import { DateTimeParseError, LocalDate } from '../../../support.js';
import type { LegacyCmdbRecord } from '../../LegacyCmdbRecord.js';
import { Environment, Tier } from '../Environment.js';

/**
 * Tłumacz między modelami: jedyne miejsce, które zna kody i formaty CMDB.
 *
 * W Javie klasa jest pakietowo-prywatna (pakiet acl, używa jej tylko
 * CmdbEnvironmentDirectory). TS nie ma widoczności pakietowej, więc moduł jest
 * eksportowany, ale traktujemy go jako wewnętrzny dla katalogu acl.
 * @internal
 */
export class CmdbTranslator {
  private constructor() {}

  static toEnvironment(record: LegacyCmdbRecord): Environment {
    return new Environment(
      record.nm,
      CmdbTranslator.tier(record.envCd),
      record.statFlg === 1,
      CmdbTranslator.date(record.lstDplyDt),
    );
  }

  private static tier(code: string | null): Tier {
    switch (code) {
      case 'P':
        return Tier.PRODUCTION;
      case 'T':
        return Tier.TEST;
      case 'D':
        return Tier.DEVELOPMENT;
      default:
        throw new IllegalArgumentError('unknown CMDB environment code: ' + String(code));
    }
  }

  private static date(text: string): LocalDate {
    try {
      return LocalDate.parseBasicDate(text);
    } catch (error) {
      if (error instanceof DateTimeParseError) {
        throw new IllegalArgumentError('invalid CMDB date: ' + text, { cause: error });
      }
      throw error;
    }
  }
}
