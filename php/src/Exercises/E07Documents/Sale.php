<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents;

use Training\Workshop\Shared\Money;

/**
 * Stabilny kontrakt sceny: sprzedaż jednego biletu z ewentualnym zakupem w barze.
 * {@code loyaltyPoints} to stan konta klienta przed tą sprzedażą.
 */
final readonly class Sale
{
    public function __construct(
        public string $number,
        public string $movie,
        public string $seat,
        public Money $ticketPrice,
        public Money $barPrice,
        public int $loyaltyPoints,
    ) {
    }
}
