import type { Connection } from './Connection.js';

export interface SecuredConnection extends Connection {
  getEncryptionAlgorithm(): string;
}
