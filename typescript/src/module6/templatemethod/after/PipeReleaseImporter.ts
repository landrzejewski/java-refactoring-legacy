import { IllegalArgumentError } from '../../../shared/errors.js';
import { type Fields, ReleaseImporter } from './ReleaseImporter.js';

export class PipeReleaseImporter extends ReleaseImporter {
  protected override parse(raw: string): Fields {
    const fields = raw.split('|');
    if (fields.length !== 2) {
      throw new IllegalArgumentError('expected releaseId and service');
    }
    return this.fields(fields[0]!.trim(), fields[1]!.trim());
  }
}
