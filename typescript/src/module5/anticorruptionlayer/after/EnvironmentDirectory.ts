import type { Environment } from './Environment.js';

/**
 * Port domenowy. Domena nie wie, że po drugiej stronie jest CMDB.
 */
export interface EnvironmentDirectory {
  all(): readonly Environment[];
}
