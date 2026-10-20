import { FtpConnectionFactory } from './ftp/FtpConnectionFactory.js';
import { Service } from './Service.js';

export function run(): void {
  const connectionFactory = new FtpConnectionFactory();
  const service = new Service(connectionFactory);
  //------------------------------------------------------------------
  service.run();
}
