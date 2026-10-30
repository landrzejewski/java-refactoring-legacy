<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use InvalidArgumentException;

/**
 * Krok 2: wybór wariantu podziału po nazwie z konfiguracji - w korzeniu kompozycji, nie w regule.
 * Warianty są bezstanowe, więc współdzielimy jedne instancje (tworzone przy pierwszym użyciu,
 * bo stała klasy w PHP nie może trzymać obiektu).
 */
final class DiscountSplits
{
    private static ?DiscountSplit $proportional = null;
    private static ?DiscountSplit $last = null;
    private static ?DiscountSplit $equal = null;

    private function __construct()
    {
    }

    public static function forMode(string $mode): DiscountSplit
    {
        return match ($mode) {
            'PROPORTIONAL' => self::$proportional ??= new ProportionalSplit(),
            'LAST' => self::$last ??= new LastTicketSplit(),
            'EQUAL' => self::$equal ??= new EqualSplit(),
            default => throw new InvalidArgumentException('unknown split mode: ' . $mode),
        };
    }
}
