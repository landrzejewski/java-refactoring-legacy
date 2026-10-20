// W Javie setText() jest package-private (@internal).
export class StatusLabel {
  private value = 'enter version';

  text(): string {
    return this.value;
  }

  /** @internal */
  setText(text: string): void {
    this.value = text;
  }
}
