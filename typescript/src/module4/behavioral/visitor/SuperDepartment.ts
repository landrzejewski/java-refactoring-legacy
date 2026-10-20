import { Department } from './Department.js';
import type { Visitor } from './Visitor.js';

export class SuperDepartment extends Department {
  constructor(name: string) {
    super(name);
  }

  override accept(visitor: Visitor): void {
    visitor.visitSuperDepartment?.(this);
    this.children.forEach((node) => node.accept(visitor));
  }
}
