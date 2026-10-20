export class TreeType {
  // Java: package-private constructor, instances are created by TreeFactory
  constructor(
    private readonly name: string,
    private readonly color: string,
  ) {}

  draw(x: number, y: number): void {
    console.log(`${this.name} (${this.color}) at ${x},${y}`);
  }
}
