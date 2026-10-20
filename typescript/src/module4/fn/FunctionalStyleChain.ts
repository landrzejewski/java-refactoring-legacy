/** Java: Function<T, R> with andThen. */
type Fn<T, R> = (value: T) => R;

function andThen<A, B, C>(first: Fn<A, B>, next: Fn<B, C>): Fn<A, C> {
  return (value) => next(first(value));
}

export function run(): void {
  const step1: Fn<string, string> = (s) => `${s} -> validate`;
  const step2: Fn<string, string> = (s) => `${s} -> authenticate`;
  const step3: Fn<string, string> = (s) => `${s} -> log`;
  const chain = andThen(andThen(step1, step2), step3);
  console.log(chain('Request'));
}
