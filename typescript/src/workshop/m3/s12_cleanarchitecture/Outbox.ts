/** Świat zewnętrzny sceny (stabilny): kanał komunikatów (np. broker) - temat i treść. */
export class Outbox {
  private readonly messageList: string[] = [];

  publish(topic: string, payload: string): void {
    this.messageList.push(`${topic}:${payload}`);
  }

  messages(): readonly string[] {
    return Object.freeze([...this.messageList]);
  }
}
