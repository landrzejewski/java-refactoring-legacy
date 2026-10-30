<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/**
 * Krok 2: koniunkcja dwóch reguł (wyrażenie nieterminalne).
 * W Javie {@code And} - w PHP {@code and} jest słowem zarezerwowanym, stąd {@code AndRule}.
 */
final readonly class AndRule implements VoucherRule
{
    public function __construct(public VoucherRule $left, public VoucherRule $right)
    {
    }

    public function applies(ScreeningFacts $facts): bool
    {
        return $this->left->applies($facts) && $this->right->applies($facts);
    }

    public function describe(): string
    {
        return $this->left->describe() . ' oraz ' . $this->right->describe();
    }
}
