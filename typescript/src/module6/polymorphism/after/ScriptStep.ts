import { IllegalArgumentError } from '../../../shared/errors.js';
import { isBlank } from '../../support.js';

export class ScriptStep {
  readonly kind = 'script';

  constructor(readonly command: string) {
    if (isBlank(command)) {
      throw new IllegalArgumentError('command must not be blank');
    }
  }

  execute(): string {
    return 'executed:' + this.command;
  }
}
