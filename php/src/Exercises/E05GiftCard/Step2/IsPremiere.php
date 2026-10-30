<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/** Krok 2: wyrażenie terminalne - seans jest premierą. */
final readonly class IsPremiere implements VoucherRule
{
    public function applies(ScreeningFacts $facts): bool
    {
        return $facts->premiere;
    }

    public function describe(): string
    {
        return 'premiera';
    }
}
