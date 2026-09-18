import { describe, expect, it } from 'vitest';
import { NullPointerError } from '../../src/shared/errors.js';
import { requireNonNull } from '../../src/shared/requireNonNull.js';

describe('requireNonNull', () => {
  it('returnsValue', () => expect(requireNonNull('x')).toBe('x'));
  it('throwsForNull', () => expect(() => requireNonNull(null, 'value')).toThrow(NullPointerError));
});
