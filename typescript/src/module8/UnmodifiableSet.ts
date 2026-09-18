import { UnsupportedOperationError } from '../shared/errors.js';

/**
 * Odpowiednik Set.copyOf / Collections.unmodifiableSet z Javy: kopia wejścia,
 * której metody modyfikujące rzucają UnsupportedOperationError.
 */
export function unmodifiableSet<T>(values: Iterable<T>): ReadonlySet<T> {
  const copy = new Set(values);
  const reject = (): never => {
    throw new UnsupportedOperationError('set is unmodifiable');
  };
  return Object.freeze(
    Object.assign(copy, { add: reject, delete: reject, clear: reject }),
  );
}
