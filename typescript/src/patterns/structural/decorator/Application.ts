import { JulLogger } from '../../JulLogger.js';
import { LowerCaseReaderDecorator } from './LowerCaseReaderDecorator.js';
import type { Reader } from './Reader.js';
import { SystemInReader } from './SystemInReader.js';
import { UnderscoreReaderDecorator } from './UnderscoreReaderDecorator.js';

const log = JulLogger.getLogger('pl.training.patterns.structural.decorator.Application');

export function run(): void {
  const reader: Reader = new LowerCaseReaderDecorator(new UnderscoreReaderDecorator(new SystemInReader()));
  //------------------------------------------------
  log.info(reader.getText(), 'main');
}
