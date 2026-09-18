import { requireNonNull } from '../../../shared/requireNonNull.js';
import { Handler } from './Handler.js';

export class Validator extends Handler {
  constructor(nextHandler: Handler) {
    super();
    this.nextHandler = nextHandler;
  }

  handleRequest(request: string): void {
    if (request.length < 3) {
      return;
    }
    requireNonNull(this.nextHandler).handleRequest(request);
  }
}
