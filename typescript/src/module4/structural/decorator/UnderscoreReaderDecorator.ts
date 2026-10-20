import type { Reader } from './Reader.js';
import { ReaderDecorator } from './ReaderDecorator.js';

export class UnderscoreReaderDecorator extends ReaderDecorator {
  constructor(reader: Reader) {
    super(reader);
  }

  override getText(): string {
    // Java regex \s = [ \t\n\x0B\f\r] (JavaScript \s would also match Unicode spaces)
    return super.getText().replace(/[ \t\n\x0B\f\r]/g, '_');
  }
}
