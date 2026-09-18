import type { Connection } from '../Connection.js';
import type { ConnectionAbstractFactory } from '../ConnectionAbstractFactory.js';
import type { SecuredConnection } from '../SecuredConnection.js';
import { FtpConnection } from './FtpConnection.js';
import { SftpConnection } from './SftpConnection.js';

export class FtpConnectionFactory implements ConnectionAbstractFactory {
  createConnection(): Connection {
    return new FtpConnection();
  }

  createSecuredConnection(): SecuredConnection {
    return new SftpConnection();
  }
}
