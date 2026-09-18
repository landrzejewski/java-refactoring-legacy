import type { Connection } from '../Connection.js';

export class HttpConnection implements Connection {
  getPort(): number {
    return 80;
  }
}
