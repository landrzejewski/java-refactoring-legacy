import { Node } from './Node.js';
import type { Visitor } from './Visitor.js';

export class Department extends Node {
  constructor(name: string) {
    super(name);
  }

  override accept(visitor: Visitor): void {
    visitor.visitDepartment?.(this);
    super.accept(visitor);
  }
}
