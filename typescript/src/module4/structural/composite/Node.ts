export abstract class Node {
  protected children: Node[] = [];

  constructor(protected readonly name: string) {}

  addChild(node: Node): void {
    this.children.push(node);
  }

  abstract printInfo(): void;

  setChildren(children: Node[]): void {
    this.children = children;
  }
}
