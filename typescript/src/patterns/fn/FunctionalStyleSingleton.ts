/** Java: Supplier<T>. */
type Supplier<T> = () => T;

export function run(): void {
  const connection = memoize(() => new DatabaseConnection()); // Java: DatabaseConnection::new
  const c1 = connection();
  const c2 = connection();
  console.log(String(c1 === c2)); // true
}

// Java: static nested class FunctionalStyleSingleton.DatabaseConnection
export class DatabaseConnection {
  constructor() {
    console.log('Connecting to DB...');
  }
}

export function memoize<T>(supplier: Supplier<T>): Supplier<T> {
  let instance: T | undefined;
  return () => {
    // Java: synchronized (this) - not needed in single-threaded JavaScript
    if (instance === undefined) {
      instance = supplier();
    }
    return instance;
  };
}
