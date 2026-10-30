<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use InvalidArgumentException;
use Training\Workshop\Shared\Money;

/** Krok 2: klient karty - bez zmian (skrypt operacji kasy). */
final class GiftCards
{
    private function __construct()
    {
    }

    /** @param list<string> $operations */
    public static function simulate(Money $initialBalance, array $operations): string
    {
        $card = new GiftCard($initialBalance);
        foreach ($operations as $operation) {
            $parts = explode(' ', trim($operation));
            match ($parts[0]) {
                'activate' => $card->activate(),
                'charge' => $card->charge(Money::of($parts[1])),
                'block' => $card->block(),
                'expire' => $card->expire(),
                default => throw new InvalidArgumentException('unknown operation: ' . $operation),
            };
        }
        return $card->status() . ' ' . $card->balance();
    }
}
