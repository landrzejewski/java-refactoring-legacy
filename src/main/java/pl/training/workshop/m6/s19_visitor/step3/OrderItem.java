package pl.training.workshop.m6.s19_visitor.step3;

/**
 * Krok 3: alternatywa Java 25 - sealed interface zamiast accept/Visitor. Wyczerpujący switch
 * po typach daje tę samą kontrolę kompilatora przy dodaniu rodzaju pozycji.
 */
public sealed interface OrderItem permits TicketItem, SnackItem, VoucherItem {
}
