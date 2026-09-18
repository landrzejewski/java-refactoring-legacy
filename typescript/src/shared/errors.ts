// Odpowiedniki wyjątków Javy używanych w przykładach.

export class IllegalArgumentError extends Error {
  override name = 'IllegalArgumentError';
}

export class IllegalStateError extends Error {
  override name = 'IllegalStateError';
}

export class UnsupportedOperationError extends Error {
  override name = 'UnsupportedOperationError';
}

export class NullPointerError extends Error {
  override name = 'NullPointerError';
}

export class ArithmeticError extends Error {
  override name = 'ArithmeticError';
}
