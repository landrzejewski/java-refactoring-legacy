<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E08Series;

use DateTimeImmutable;
use Training\Workshop\Shared\Money;

/**
 * Stabilny kontrakt ćwiczenia: zamówienie serii tygodniowej seansów z jednego szablonu.
 * Pola {@code hall}, {@code basePrice}, {@code dubbing} i {@code ageLimit} mogą być {@code null}
 * (wtedy obowiązują wartości domyślne kina). {@code premiereWeek} dolicza dopłatę PREMIERA tylko
 * w pierwszym tygodniu, {@code lastWeekHall} przenosi ostatni seans serii do innej sali.
 */
final readonly class SeriesRequest
{
    public function __construct(
        public string $title,
        public string $format,
        public ?string $hall,
        public DateTimeImmutable $firstStart,
        public int $weeks,
        public ?Money $basePrice,
        public ?bool $dubbing,
        public ?int $ageLimit,
        public bool $premiereWeek,
        public ?string $lastWeekHall,
    ) {
    }
}
