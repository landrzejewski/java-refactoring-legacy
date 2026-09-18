import type { SecuredConnection } from '../SecuredConnection.js';

export class SftpConnection implements SecuredConnection {
  getPort(): number {
    return 22;
  }

  getEncryptionAlgorithm(): string {
    return 'AES';
  }
}
