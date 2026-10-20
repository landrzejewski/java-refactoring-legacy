// Odpowiednik Subscription extends AutoCloseable.
export interface Subscription {
  close(): void;
}
