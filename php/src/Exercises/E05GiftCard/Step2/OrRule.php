<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/**
 * Krok 2: alternatywa dwóch reguł. Tekstowa składnia z konfiguracji jeszcze jej nie zna,
 * ale w kodzie można ją już składać - nowy węzeł nie wymaga zmiany parsera ani Vouchers.
 * W Javie {@code Or} - w PHP {@code or} jest słowem zarezerwowanym, stąd {@code OrRule}.
 */
final readonly class OrRule implements VoucherRule
{
    public function __construct(public VoucherRule $left, public VoucherRule $right)
    {
    }

    public function applies(ScreeningFacts $facts): bool
    {
        return $this->left->applies($facts) || $this->right->applies($facts);
    }

    public function describe(): string
    {
        return $this->left->describe() . ' lub ' . $this->right->describe();
    }
}
