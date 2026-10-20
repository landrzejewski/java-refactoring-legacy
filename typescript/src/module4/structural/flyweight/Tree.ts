import type { TreeType } from './TreeType.js';

export class Tree {
  constructor(
    private readonly x: number,
    private readonly y: number,
    private readonly type: TreeType,
  ) {}

  draw(): void {
    this.type.draw(this.x, this.y);
  }
}
