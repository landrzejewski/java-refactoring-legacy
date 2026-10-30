<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard;

/**
 * Stabilny kontrakt sceny: fakty o seansie, do których odwołuje się reguła vouchera
 * (format seansu, dzień tygodnia, czy to premiera).
 */
final readonly class ScreeningFacts
{
    public function __construct(public string $format, public DayOfWeek $day, public bool $premiere)
    {
    }
}
