import type { Connection } from './Connection.js';
import type { SecuredConnection } from './SecuredConnection.js';

export interface ConnectionAbstractFactory {
  createConnection(): Connection;
  createSecuredConnection(): SecuredConnection;
}
