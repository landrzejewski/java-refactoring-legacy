import { NullPointerError } from './errors.js';

// Odpowiednik Objects.requireNonNull(value, message).
export function requireNonNull<T>(value: T | null | undefined, message?: string): T {
  if (value === null || value === undefined) {
    throw new NullPointerError(message);
  }
  return value;
}
