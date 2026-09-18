export abstract class Processor<V, PV> {
  /** Java: public final void run() - the template method; subclasses must not override it. */
  run(): void {
    const data = this.read();
    const result = this.process(data);
    this.write(result);
  }

  protected abstract read(): V;

  protected abstract process(data: V): PV;

  protected abstract write(data: PV): void;
}
