import { JulLogger } from '../../JulLogger.js';
import { Window } from './Window.js';

const log = JulLogger.getLogger('pl.training.patterns.creational.prototype.Application');

export function run(): void {
  const fullScreenWindow = new Window(0, 0, 800, 600);
  const dialog = new Window(200, 200, 100, 50);
  //----------------------------------------------------------
  const window = fullScreenWindow.clone();
  log.info(`Window: ${window}`, 'main');
}
