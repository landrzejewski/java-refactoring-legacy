<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/**
 * Krok 2: Vouchers nie zna już składni tekstu - parser buduje drzewo raz, a applies i describe
 * to dwie operacje na tym samym drzewie. Nowe pojęcie w regule to nowy węzeł i jedna gałąź
 * w parserze, bez dotykania tej klasy.
 */
final class Vouchers
{
    private function __construct()
    {
    }

    public static function applies(?string $rule, ScreeningFacts $facts): bool
    {
        return VoucherRuleParser::parse($rule)->applies($facts);
    }

    public static function describe(?string $rule): string
    {
        return VoucherRuleParser::parse($rule)->describe();
    }
}
