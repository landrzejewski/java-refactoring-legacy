import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';
import { ReleaseDraft } from './ReleaseDraft.js';

// Odpowiednik chronionego rekordu ReleaseImporter.Fields — wynik kroku parse().
export class Fields {
  constructor(
    readonly releaseId: string | undefined,
    readonly service: string | undefined,
  ) {}
}

export abstract class ReleaseImporter {
  // Metoda szablonowa. W Javie jest `final`; TS nie ma `final` dla metod,
  // więc kontrakt „podklasy nie nadpisują importRelease” pilnuje test.
  importRelease(raw: string): ReleaseDraft {
    if (isBlank(raw)) {
      throw new IllegalArgumentError('input must not be blank');
    }

    const fields = this.parse(raw);
    const releaseId = fields.releaseId;
    const service = fields.service;
    if (releaseId === undefined || isBlank(releaseId) || service === undefined || isBlank(service)) {
      throw new IllegalArgumentError('releaseId and service are required');
    }
    return new ReleaseDraft(releaseId, service);
  }

  protected abstract parse(raw: string): Fields;

  protected fields(releaseId: string | undefined, service: string | undefined): Fields {
    return new Fields(releaseId, service);
  }
}
