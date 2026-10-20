import { UnsupportedOperationError } from '../../../shared/errors.js';
import { Node } from './Node.js';
import type { Visitor } from './Visitor.js';

export class Employee extends Node {
  constructor(name: string) {
    super(name);
  }

  override addChild(_node: Node): void {
    throw new UnsupportedOperationError();
  }

  override accept(visitor: Visitor): void {
    visitor.visitEmployee?.(this);
  }
}
