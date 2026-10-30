<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Start;

use Training\Workshop\Exercises\E04SeatPicker\Seat;

/**
 * Jeden rekord na wszystkie rodzaje zmian: które pola coś znaczą, zależy od {@code op}.
 *
 * Różnica względem Javy: PHP nie ma klas zagnieżdżonych, więc prywatny rekord {@code SeatPicker.Change}
 * jest osobną klasą w tej samej przestrzeni nazw; używa go tylko {@see SeatPicker}.
 */
final readonly class Change
{
    /** @param ?list<Seat> $seatsBefore */
    public function __construct(
        public string $op,
        public ?Seat $seat,
        public ?Seat $other,
        public int $index,
        public bool $flag,
        public ?string $text,
        public ?array $seatsBefore,
    ) {
    }
}
