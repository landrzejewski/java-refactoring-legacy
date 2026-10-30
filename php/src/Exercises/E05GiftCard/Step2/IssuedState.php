<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use LogicException;
use Training\Workshop\Shared\Money;

/** Krok 2: karta wydana, jeszcze nieaktywna - można ją aktywować, zablokować albo wygasić. */
final class IssuedState implements CardState
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
        return 'ISSUED';
    }

    public function activate(): CardState
    {
        return ActiveState::instance();
    }

    public function charge(GiftCard $card, Money $amount): CardState
    {
        throw new LogicException('card not activated');
    }

    public function block(): CardState
    {
        return BlockedState::instance();
    }

    public function expire(): CardState
    {
        return ExpiredState::instance();
    }
}
