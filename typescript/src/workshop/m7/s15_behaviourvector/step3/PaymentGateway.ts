import type { Money } from '../../../shared/Money.js';

/** Seam dla efektu ubocznego "obciążenie karty". */
export type PaymentGateway = (card: string, amount: Money) => boolean;
