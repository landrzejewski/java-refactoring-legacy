import type { SecuredConnection } from '../SecuredConnection.js';

export class HttpsConnection implements SecuredConnection {
  getPort(): number {
    return 443;
  }

  getEncryptionAlgorithm(): string {
    return 'AES';
  }
}
