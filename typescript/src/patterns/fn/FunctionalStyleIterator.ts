export function run(): void {
  // Java: Stream.of(...).map(String::toUpperCase).forEach(System.out::println)
  ['one', 'two', 'three']
    .map((s) => s.toUpperCase())
    .forEach((s) => console.log(s));
}
