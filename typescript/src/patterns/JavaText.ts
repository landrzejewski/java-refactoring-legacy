/**
 * Helpers reproducing Java's toString() conventions where they differ from JavaScript
 * (double always has a fractional part, LocalDateTime ISO format).
 */
export function javaDouble(value: number): string {
  if (Number.isNaN(value)) {
    return 'NaN';
  }
  if (!Number.isFinite(value)) {
    return value > 0 ? 'Infinity' : '-Infinity';
  }
  if (Object.is(value, -0)) {
    return '-0.0';
  }
  const magnitude = Math.abs(value);
  if (magnitude === 0 || (magnitude >= 1e-3 && magnitude < 1e7)) {
    const text = String(value);
    return text.includes('.') ? text : `${text}.0`;
  }
  // scientific notation: Java "1.0E7", JavaScript "1e+7"
  const [mantissa = '', exponent = ''] = value.toExponential().split('e');
  return `${mantissa.includes('.') ? mantissa : `${mantissa}.0`}E${exponent.replace('+', '')}`;
}

/** Java: LocalDateTime.now().toString() (millisecond precision in JavaScript). */
export function javaLocalDateTime(date: Date): string {
  const pad = (value: number, length = 2): string => String(value).padStart(length, '0');
  const text = `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
  const seconds = date.getSeconds();
  const millis = date.getMilliseconds();
  if (seconds === 0 && millis === 0) {
    return text;
  }
  return millis === 0 ? `${text}:${pad(seconds)}` : `${text}:${pad(seconds)}.${pad(millis, 3)}`;
}
