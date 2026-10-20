import { Node } from './Node.js';

export class Department extends Node {
  constructor(name: string) {
    super(name);
  }

  printInfo(): void {
    console.log(`Department: ${this.name}`);
    this.children.forEach((node) => node.printInfo());
  }
}
