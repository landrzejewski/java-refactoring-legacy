<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step1;

use Training\Workshop\Shared\Money;

/**
 * Krok 1: kontrakt stanu karty. Każde przejście zwraca stan następny albo rzuca ten sam wyjątek
 * z tym samym komunikatem co start. Stany są bezstanowe (saldo zostaje w karcie), więc każdy
 * ma jedną współdzieloną instancję.
 */
interface CardState
{
    public function name(): string;

    public function activate(): CardState;

    public function charge(GiftCard $card, Money $amount): CardState;

    public function block(): CardState;

    public function expire(): CardState;
}
