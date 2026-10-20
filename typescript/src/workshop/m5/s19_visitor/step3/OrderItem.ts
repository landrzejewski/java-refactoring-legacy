import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

/**
 * Krok 3: alternatywa - zamknięta unia dyskryminowana (odpowiednik sealed interface z Javy 25)
 * zamiast accept/Visitor. Wyczerpujący switch po kind daje tę samą kontrolę kompilatora
 * przy dodaniu rodzaju pozycji.
 */
export type OrderItem = TicketItem | SnackItem | VoucherItem;
