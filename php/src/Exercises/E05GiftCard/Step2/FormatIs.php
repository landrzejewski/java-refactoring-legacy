<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/** Krok 2: wyrażenie terminalne - seans ma dany format (2D, 3D, IMAX). */
final readonly class FormatIs implements VoucherRule
{
    public function __construct(public string $format)
    {
    }

    public function applies(ScreeningFacts $facts): bool
    {
        return $facts->format === $this->format;
    }

    public function describe(): string
    {
        return 'format ' . $this->format;
    }
}
