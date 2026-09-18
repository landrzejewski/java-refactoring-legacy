/** Java: Function<T, R> with andThen. */
type Fn<T, R> = (value: T) => R;

function andThen<A, B, C>(first: Fn<A, B>, next: Fn<B, C>): Fn<A, C> {
  return (value) => next(first(value));
}

export function run(): void {
  const base: Fn<string, string> = (s) => s;
  const trim: Fn<string, string> = (s) => s.trim();
  const upper: Fn<string, string> = (s) => s.toUpperCase();
  const addBrackets: Fn<string, string> = (s) => `[${s}]`;
  const decorated =
    andThen(andThen(andThen(base, trim),
      upper),
    addBrackets);
  console.log(decorated('   decorator pattern   '));
}
