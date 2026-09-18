import { IllegalArgumentError } from '../../../shared/errors.js';
import { splitInTwo } from '../../support.js';
import { type Fields, ReleaseImporter } from './ReleaseImporter.js';

export class KeyValueReleaseImporter extends ReleaseImporter {
  protected override parse(raw: string): Fields {
    let releaseId: string | undefined;
    let service: string | undefined;
    for (const field of raw.split(';')) {
      const pair = splitInTwo(field, '=');
      if (pair.length !== 2) {
        throw new IllegalArgumentError('expected key=value');
      }
      const [key, value] = pair as [string, string];
      switch (key.trim()) {
        case 'id':
          releaseId = value.trim();
          break;
        case 'service':
          service = value.trim();
          break;
        default:
          throw new IllegalArgumentError('unknown field: ' + key);
      }
    }
    return this.fields(releaseId, service);
  }
}
