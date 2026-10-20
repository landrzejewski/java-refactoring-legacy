import type { Connection } from '../Connection.js';
import type { ConnectionAbstractFactory } from '../ConnectionAbstractFactory.js';
import type { SecuredConnection } from '../SecuredConnection.js';
import { HttpConnection } from './HttpConnection.js';
import { HttpsConnection } from './HttpsConnection.js';

export class HttpConnectionFactory implements ConnectionAbstractFactory {
  createConnection(): Connection {
    return new HttpConnection();
  }

  createSecuredConnection(): SecuredConnection {
    return new HttpsConnection();
  }
}
