import type { Reader } from './Reader.js';
import { ReaderDecorator } from './ReaderDecorator.js';

export class LowerCaseReaderDecorator extends ReaderDecorator {
  constructor(reader: Reader) {
    super(reader);
  }

  override getText(): string {
    return super.getText().toLowerCase();
  }
}
