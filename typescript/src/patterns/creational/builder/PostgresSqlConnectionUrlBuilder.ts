import type { ConnectionUrl } from './ConnectionUrl.js';
import type { ConnectionUrlBuilder } from './ConnectionUrlBuilder.js';
import { GenericConnectionUrlBuilder } from './GenericConnectionUrlBuilder.js';

export class PostgresSqlConnectionUrlBuilder implements ConnectionUrlBuilder {
  private static readonly PROTOCOL = 'postgres';
  private static readonly PORT = 5432;
  private readonly builder = new GenericConnectionUrlBuilder();

  constructor() {
    this.builder.protocol(PostgresSqlConnectionUrlBuilder.PROTOCOL);
    this.builder.port(PostgresSqlConnectionUrlBuilder.PORT);
  }

  build(): ConnectionUrl {
    return this.builder.build();
  }

  host(host: string): this {
    this.builder.host(host);
    return this;
  }

  port(port: number): this {
    this.builder.port(port);
    return this;
  }

  protocol(protocol: string): this {
    this.builder.protocol(protocol);
    return this;
  }

  database(database: string): this {
    this.builder.database(database);
    return this;
  }

  encoding(encoding: string): this {
    this.builder.encoding(encoding);
    return this;
  }
}
