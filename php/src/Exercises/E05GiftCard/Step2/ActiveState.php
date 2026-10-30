<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use LogicException;
use Training\Workshop\Shared\Money;

/** Krok 2: karta aktywna - jedyny stan, w którym obciążenie zmienia saldo; przy zerze przechodzi w EMPTY. */
final class ActiveState implements CardState
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
        return 'ACTIVE';
    }

    public function activate(): CardState
    {
        throw new LogicException('card already active');
    }

    public function charge(GiftCard $card, Money $amount): CardState
    {
        if ($amount->isGreaterThan($card->balance())) {
            throw new LogicException('insufficient balance');
        }
        $card->debit($amount);
        return $card->balance()->compareTo(Money::zero()) === 0 ? EmptyState::instance() : $this;
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
