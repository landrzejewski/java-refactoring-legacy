export class Logger {
  private static LOGGER: Logger | null = null;

  private constructor() {}

  // Java: synchronized - not needed in single-threaded JavaScript
  static getInstance(): Logger {
    if (Logger.LOGGER === null) {
      Logger.LOGGER = new Logger();
    }
    return Logger.LOGGER;
  }

  log(message: string): void {
    console.log(`Info: ${message}`);
  }
}
