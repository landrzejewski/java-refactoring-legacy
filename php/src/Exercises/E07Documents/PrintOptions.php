<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents;

/**
 * Stabilny kontrakt sceny: opcjonalne sekcje dokumentu, które klient może dołączyć.
 * Stałe {@code NONE}/{@code ALL} z Javy to metody {@see none()}/{@see all()} (stała klasy w PHP nie może być obiektem).
 */
final readonly class PrintOptions
{
    public function __construct(public bool $withVat, public bool $withPromo, public bool $withLoyaltyFooter)
    {
    }

    public static function none(): self
    {
        return new self(false, false, false);
    }

    public static function all(): self
    {
        return new self(true, true, true);
    }
}
