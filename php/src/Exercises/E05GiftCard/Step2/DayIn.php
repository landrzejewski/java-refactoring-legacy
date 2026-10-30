<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\DayOfWeek;
use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/** Krok 2: wyrażenie terminalne - dzień seansu należy do listy (w kolejności z reguły). */
final readonly class DayIn implements VoucherRule
{
    /** @param list<DayOfWeek> $days */
    public function __construct(public array $days)
    {
    }

    public function applies(ScreeningFacts $facts): bool
    {
        return in_array($facts->day, $this->days, true);
    }

    public function describe(): string
    {
        return 'dzień: ' . implode(', ', array_map(fn (DayOfWeek $day) => substr($day->name, 0, 3), $this->days));
    }
}
