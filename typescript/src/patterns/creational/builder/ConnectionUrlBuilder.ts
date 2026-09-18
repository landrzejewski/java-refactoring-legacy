import type { ConnectionUrl } from './ConnectionUrl.js';

export interface ConnectionUrlBuilder {
  host(host: string): ConnectionUrlBuilder;
  port(port: number): ConnectionUrlBuilder;
  protocol(protocol: string): ConnectionUrlBuilder;
  database(database: string): ConnectionUrlBuilder;
  encoding(encoding: string): ConnectionUrlBuilder;
  build(): ConnectionUrl;
}
