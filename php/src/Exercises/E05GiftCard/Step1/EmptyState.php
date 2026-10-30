<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step1;

use LogicException;
use Training\Workshop\Shared\Money;

/** Krok 1: karta wyczerpana - jedyne dozwolone przejście to wygaśnięcie. */
final class EmptyState implements CardState
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
        return 'EMPTY';
    }

    public function activate(): CardState
    {
        throw new LogicException('card is empty');
    }

    public function charge(GiftCard $card, Money $amount): CardState
    {
        throw new LogicException('card is empty');
    }

    public function block(): CardState
    {
        throw new LogicException('card is empty');
    }

    public function expire(): CardState
    {
        return ExpiredState::instance();
    }
}
