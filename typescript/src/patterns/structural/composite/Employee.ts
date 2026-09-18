import { UnsupportedOperationError } from '../../../shared/errors.js';
import { Node } from './Node.js';

export class Employee extends Node {
  constructor(name: string) {
    super(name);
  }

  override addChild(_node: Node): void {
    throw new UnsupportedOperationError();
  }

  printInfo(): void {
    console.log(` - Employee ${this.name}`);
  }
}
