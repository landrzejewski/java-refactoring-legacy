<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/** Krok 2: negacja reguły. */
final readonly class Not implements VoucherRule
{
    public function __construct(public VoucherRule $rule)
    {
    }

    public function applies(ScreeningFacts $facts): bool
    {
        return !$this->rule->applies($facts);
    }

    public function describe(): string
    {
        return 'nie ' . $this->rule->describe();
    }
}
