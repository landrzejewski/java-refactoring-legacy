<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Start;

use InvalidArgumentException;
use Training\Workshop\Shared\Money;

/**
 * Start: klient karty - kasa wykonuje skrypt operacji (activate, charge &lt;kwota&gt;, block, expire)
 * i raportuje status oraz saldo. Przez niego woła test.
 */
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
