import { IllegalStateError } from '../../../shared/errors.js';
import { JulLogger } from '../../JulLogger.js';
import { Order } from './Order.js';

const log = JulLogger.getLogger('pl.training.patterns.behavioral.state.Application');

export function run(): void {
  const order = new Order();
  log.info(`State: ${String(order.getState())}`, 'main');
  order.pay();
  log.info(`State: ${String(order.getState())}`, 'main');
  order.ship();
  log.info(`State: ${String(order.getState())}`, 'main');
  try {
    order.cancel();
  } catch (exception) {
    if (!(exception instanceof IllegalStateError)) {
      throw exception;
    }
    log.info(exception.message, 'main');
  }
}
