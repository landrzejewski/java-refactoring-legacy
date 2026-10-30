<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use LogicException;
use Training\Workshop\Shared\Money;

/** Krok 2: karta wygasła - stan końcowy, każda operacja kończy się tym samym błędem. */
final class ExpiredState implements CardState
{
    private static ?CardState $instance = null;

    private function __construct()
    {
    }

    public static function instance(): CardState
    {
        return self::$instance ??= new self();
    }

    public function name(): string
    {
        return 'EXPIRED';
    }

    public function activate(): CardState
    {
        throw new LogicException('card has expired');
    }

    public function charge(GiftCard $card, Money $amount): CardState
    {
        throw new LogicException('card has expired');
    }

    public function block(): CardState
    {
        throw new LogicException('card has expired');
    }

    public function expire(): CardState
    {
        throw new LogicException('card has expired');
    }
}
