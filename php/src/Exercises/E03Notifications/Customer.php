<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications;

/**
 * Stabilny kontrakt sceny: klient z biletem albo z listy oczekujacych. Kazdy kanal kontaktu
 * moze byc {@code null} albo pusty; token push zaczynajacy sie od {@code expired-} jest niewazny.
 */
final readonly class Customer
{
    public function __construct(
        public string $name,
        public ?string $email,
        public ?string $phone,
        public ?string $pushToken,
    ) {
    }
}
