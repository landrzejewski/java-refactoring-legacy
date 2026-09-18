export abstract class Handler {
  protected nextHandler: Handler | null = null;

  abstract handleRequest(request: string): void;
}
