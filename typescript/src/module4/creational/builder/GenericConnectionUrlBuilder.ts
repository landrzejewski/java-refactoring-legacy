import { ConnectionUrl } from './ConnectionUrl.js';
import type { ConnectionUrlBuilder } from './ConnectionUrlBuilder.js';

export class GenericConnectionUrlBuilder implements ConnectionUrlBuilder {
  protected static readonly ENCODING = 'UTF-8';
  protected readonly connectionUrl = new ConnectionUrl();

  constructor() {
    this.connectionUrl.encoding = GenericConnectionUrlBuilder.ENCODING;
  }

  build(): ConnectionUrl {
    return this.connectionUrl;
  }

  host(host: string): this {
    this.connectionUrl.host = host;
    return this;
  }

  port(port: number): this {
    this.connectionUrl.port = port;
    return this;
  }

  protocol(protocol: string): this {
    this.connectionUrl.protocol = protocol;
    return this;
  }

  database(database: string): this {
    this.connectionUrl.database = database;
    return this;
  }

  encoding(encoding: string): this {
    this.connectionUrl.encoding = encoding;
    return this;
  }
}
