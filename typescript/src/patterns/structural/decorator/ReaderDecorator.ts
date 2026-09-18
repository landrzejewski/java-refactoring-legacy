import type { Reader } from './Reader.js';

export abstract class ReaderDecorator implements Reader {
  constructor(protected readonly reader: Reader) {}

  getText(): string {
    return this.reader.getText();
  }
}
