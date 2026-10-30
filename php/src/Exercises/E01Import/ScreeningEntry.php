<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import;

use DateTimeImmutable;
use Stringable;
use Training\Workshop\Shared\Money;

/** Pozycja zaimportowanego repertuaru: seans z wyliczoną ceną bazową biletu normalnego. */
final readonly class ScreeningEntry implements Stringable
{
    public function __construct(
        public DateTimeImmutable $start,
        public string $title,
        public string $format,
        public string $hall,
        public Money $price,
    ) {
    }

    public function __toString(): string
    {
        return $this->start->format('H:i') . ' ' . $this->title . ' (' . $this->format . ', sala ' . $this->hall . ') ' . $this->price;
    }
}
