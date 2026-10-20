export class ConnectionUrl {
  // package-private fields in Java, set directly by the builders
  host: string | null = null;
  port = 0;
  protocol: string | null = null;
  database: string | null = null;
  encoding: string | null = null;

  toString(): string {
    return `jdbc:${this.protocol}://${this.host}:${this.port}/${this.database}?encoding=${this.encoding}`;
  }

  getHost(): string | null {
    return this.host;
  }

  getPort(): number {
    return this.port;
  }

  getProtocol(): string | null {
    return this.protocol;
  }

  getDatabase(): string | null {
    return this.database;
  }

  getEncoding(): string | null {
    return this.encoding;
  }
}
