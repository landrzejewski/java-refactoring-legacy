export interface Expression {
  evaluate(context: ReadonlyMap<string, number>): number;
}
