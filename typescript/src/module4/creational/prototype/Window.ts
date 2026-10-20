// Java: public final class Window implements Cloneable
export class Window {
  constructor(
    private readonly x: number,
    private readonly y: number,
    private readonly width: number,
    private readonly height: number,
  ) {}

  /** Java: (Window) super.clone() - a shallow field-by-field copy. */
  clone(): Window {
    return Object.assign(Object.create(Object.getPrototypeOf(this) as object) as Window, this);
  }

  getX(): number {
    return this.x;
  }

  getY(): number {
    return this.y;
  }

  getWidth(): number {
    return this.width;
  }

  getHeight(): number {
    return this.height;
  }

  equals(o: unknown): boolean {
    if (o === this) return true;
    if (!(o instanceof Window)) return false;
    if (this.getX() !== o.getX()) return false;
    if (this.getY() !== o.getY()) return false;
    if (this.getWidth() !== o.getWidth()) return false;
    if (this.getHeight() !== o.getHeight()) return false;
    return true;
  }

  hashCode(): number {
    const PRIME = 59;
    let result = 1;
    // Math.imul + |0 reproduce Java int overflow
    result = (Math.imul(result, PRIME) + this.getX()) | 0;
    result = (Math.imul(result, PRIME) + this.getY()) | 0;
    result = (Math.imul(result, PRIME) + this.getWidth()) | 0;
    result = (Math.imul(result, PRIME) + this.getHeight()) | 0;
    return result;
  }

  toString(): string {
    return `Window(x=${this.getX()}, y=${this.getY()}, width=${this.getWidth()}, height=${this.getHeight()})`;
  }
}
