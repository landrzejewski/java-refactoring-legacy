import { JulLogger } from '../../JulLogger.js';
import { Logger } from './Logger.js';

const log = JulLogger.getLogger('pl.training.module4.creational.singleton.Application');

export function run(): void {
  const logger = Logger.getInstance();
  log.info(`Is same: ${logger === Logger.getInstance()}`, 'main');
  logger.log('Success');
}
