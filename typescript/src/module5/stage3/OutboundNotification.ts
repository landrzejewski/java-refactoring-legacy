// Odpowiednik @FunctionalInterface: rola, którą klient może spełnić dowolnym obiektem.
export interface OutboundNotification {
  dispatch(successful: boolean): string;
}
