<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use Training\Workshop\Exercises\E04SeatPicker\Seat;

/**
 * Migawka koszyka: tylko koszyk ją tworzy i tylko koszyk ją czyta.
 *
 * Różnica względem Javy: PHP nie ma klas zagnieżdżonych, więc {@code Cart.Memento} jest osobną klasą
 * z prywatnymi polami i prywatnym konstruktorem. {@see Cart} sięga do nich domknięciem
 * uruchomionym w zasięgu tej klasy ({@see \Closure::bind()}, {@see \Closure::call()}) - z zewnątrz
 * migawka pozostaje nieprzezroczysta.
 */
final readonly class Memento
{
    /** @param list<Seat> $seats */
    private function __construct(private array $seats, private bool $ownGlasses, private ?string $voucher)
    {
    }
}
