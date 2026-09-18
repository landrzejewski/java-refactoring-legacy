import type { Connection } from '../Connection.js';

export class FtpConnection implements Connection {
  getPort(): number {
    return 21;
  }
}
