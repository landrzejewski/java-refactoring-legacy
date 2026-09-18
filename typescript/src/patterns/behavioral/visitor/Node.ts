import type { Visitor } from './Visitor.js';

export abstract class Node {
  protected children: Node[] = [];

  // Java: protected final String name (read by visitors in the same package)
  constructor(readonly name: string) {}

  addChild(node: Node): void {
    this.children.push(node);
  }

  accept(visitor: Visitor): void {
    this.children.forEach((node) => node.accept(visitor));
  }

  setChildren(children: Node[]): void {
    this.children = children;
  }
}
