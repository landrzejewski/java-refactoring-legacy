export class ServerEvent {
  constructor(private payload: string | null) {}

  static builder(): ServerEventBuilder {
    return new ServerEventBuilder();
  }

  getPayload(): string | null {
    return this.payload;
  }
}

// Java: public static nested class ServerEvent.ServerEventBuilder (Lombok-generated)
export class ServerEventBuilder {
  private payloadValue: string | null = null;

  /**
   * @return this.
   */
  payload(payload: string | null): ServerEventBuilder {
    this.payloadValue = payload;
    return this;
  }

  build(): ServerEvent {
    return new ServerEvent(this.payloadValue);
  }

  toString(): string {
    return `ServerEvent.ServerEventBuilder(payload=${this.payloadValue})`;
  }
}
