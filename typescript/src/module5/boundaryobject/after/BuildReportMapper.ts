import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank, parseDouble, parseInteger } from '../../support.js';
import { BuildReport, Status } from './BuildReport.js';

/**
 * Jedyne miejsce, które zna format ładunku CI. Tłumaczy go na BuildReport
 * albo odrzuca na granicy z jednym, czytelnym wyjątkiem.
 *
 * W Javie klasa jest pakietowo-prywatna (używa jej tylko CiBuildReportsAdapter).
 * TS nie ma widoczności pakietowej, więc moduł jest eksportowany, ale traktujemy go
 * jako wewnętrzny dla boundaryobject/after.
 * @internal
 */
export class BuildReportMapper {
  private constructor() {}

  static fromPayload(payload: ReadonlyMap<string, string> | null | undefined): BuildReport {
    if (payload == null) {
      throw new IllegalArgumentError('invalid CI payload: missing');
    }
    try {
      return new BuildReport(
        BuildReportMapper.required(payload, 'id'),
        BuildReportMapper.status(BuildReportMapper.required(payload, 'status').toUpperCase()),
        parseDouble(BuildReportMapper.required(payload, 'coverage')),
        parseInteger(BuildReportMapper.required(payload, 'failed')),
        BuildReportMapper.required(payload, 'commit').toLowerCase(),
      );
    } catch (error) {
      // NumberFormatError jest podklasą IllegalArgumentError, jak w Javie.
      if (error instanceof IllegalArgumentError) {
        throw new IllegalArgumentError('invalid CI payload: ' + error.message, { cause: error });
      }
      throw error;
    }
  }

  private static required(payload: ReadonlyMap<string, string>, key: string): string {
    const value = payload.get(key);
    if (value === undefined || isBlank(value)) {
      throw new IllegalArgumentError('missing ' + key);
    }
    return value.trim();
  }

  // Odpowiednik BuildReport.Status.valueOf(String).
  private static status(name: string): Status {
    const status = Object.values(Status).find(candidate => candidate === name);
    if (status === undefined) {
      throw new IllegalArgumentError('No enum constant BuildReport.Status.' + name);
    }
    return status;
  }
}
