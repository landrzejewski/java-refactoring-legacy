import { GenericConnectionUrlBuilder } from './GenericConnectionUrlBuilder.js';

export class MySqlConnectionUrlBuilder extends GenericConnectionUrlBuilder {
  private static readonly PROTOCOL = 'mysql';
  private static readonly PORT = 3306;

  constructor() {
    super();
    this.connectionUrl.protocol = MySqlConnectionUrlBuilder.PROTOCOL;
    this.connectionUrl.port = MySqlConnectionUrlBuilder.PORT;
  }
}
