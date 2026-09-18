const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] as const;

/**
 * Minimal stand-in for java.util.logging.Logger with the default ConsoleHandler/SimpleFormatter:
 * writes two lines to standard error, e.g.
 *
 *   Sep 18, 2026 10:30:36 PM pl.training.patterns.creational.builder.Director run
 *   INFO: Connection url: ...
 *
 * JUL infers the calling method from the stack; here it is passed explicitly (sourceMethod).
 */
export class JulLogger {
  private constructor(private readonly name: string) {}

  /** name = fully qualified Java class name, so log headers match the Java output. */
  static getLogger(name: string): JulLogger {
    return new JulLogger(name);
  }

  info(message: string | null | undefined, sourceMethod: string): void {
    console.error(`${formatTimestamp(new Date())} ${this.name} ${sourceMethod}`);
    console.error(`INFO: ${message ?? 'null'}`);
  }
}

// Java SimpleFormatter default: "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp" (English month names)
function formatTimestamp(date: Date): string {
  const hours = date.getHours();
  const hour12 = hours % 12 === 0 ? 12 : hours % 12;
  const pad = (value: number): string => String(value).padStart(2, '0');
  return `${MONTHS[date.getMonth()]} ${pad(date.getDate())}, ${date.getFullYear()} ` +
    `${hour12}:${pad(date.getMinutes())}:${pad(date.getSeconds())} ${hours < 12 ? 'AM' : 'PM'}`;
}
