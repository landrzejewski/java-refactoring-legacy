/** Java: Supplier<T>. */
type Supplier<T> = () => T;

// Java: nested interface/classes of FunctionalStyleFactory
export interface Shape {
  draw(): void;
}

export class Circle implements Shape {
  draw(): void {
    console.log('Circle');
  }
}

export class Square implements Shape {
  draw(): void {
    console.log('Square');
  }
}

export function run(): void {
  const circleFactory: Supplier<Shape> = () => new Circle();
  const squareFactory: Supplier<Shape> = () => new Square(); // Java: Square::new
  const s1 = circleFactory();
  const s2 = squareFactory();
  s1.draw();
  s2.draw();
}
