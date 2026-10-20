/** Java: Function<T, R>. */
type Fn<T, R> = (value: T) => R;

export function run(): void {
  const upperCaseStrategy: Fn<string, string> = (s) => s.toUpperCase();
  const lowerCaseStrategy: Fn<string, string> = (s) => s.toLowerCase();
  const reverseStrategy: Fn<string, string> = (s) => [...s].reverse().join(''); // Java: new StringBuilder(s).reverse()

  const input = 'Functional Patterns';

  executeStrategy('UPPER', input, upperCaseStrategy);
  executeStrategy('LOWER', input, lowerCaseStrategy);
  executeStrategy('REVERSE', input, reverseStrategy);
}

export function executeStrategy(name: string, input: string, strategy: Fn<string, string>): void {
  console.log(`${name}: ${strategy(input)}`);
}
