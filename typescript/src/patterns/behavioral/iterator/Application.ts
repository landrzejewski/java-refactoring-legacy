export function run(): void {
  const collection: readonly number[] = Object.freeze([1, 2, 3, 4, 5]);
  const iterator = collection[Symbol.iterator]();
  //------------------------------------------------------
  for (let result = iterator.next(); !result.done; result = iterator.next()) { // Java: hasNext()/next()
    console.log(String(result.value));
  }
}
