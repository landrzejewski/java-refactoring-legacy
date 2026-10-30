<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use InvalidArgumentException;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: karta trzyma obiekt stanu zamiast tekstu i tylko do niego deleguje. Wspólna walidacja
 * (saldo początkowe, dodatnia kwota) zostaje w karcie, bo nie zależy od stanu. Tabela przejść
 * jest teraz w klasach stanów: jeden stan = jedna klasa, jeden wiersz tabeli.
 */
final class GiftCard
{
    private CardState $state;
    private Money $balance;

    public function __construct(Money $initialBalance)
    {
        if (!$initialBalance->isGreaterThan(Money::zero())) {
            throw new InvalidArgumentException('initial balance must be positive');
        }
        $this->state = IssuedState::instance();
        $this->balance = $initialBalance;
    }

    public function activate(): void
    {
        $this->state = $this->state->activate();
    }

    public function charge(Money $amount): void
    {
        if (!$amount->isGreaterThan(Money::zero())) {
            throw new InvalidArgumentException('amount must be positive');
        }
        $this->state = $this->state->charge($this, $amount);
    }

    public function block(): void
    {
        $this->state = $this->state->block();
    }

    public function expire(): void
    {
        $this->state = $this->state->expire();
    }

    /**
     * Przeznaczone tylko dla stanów z tej przestrzeni nazw - stan aktywny zmniejsza saldo
     * (PHP nie ma widoczności pakietowej, więc metoda jest publiczna).
     *
     * @internal
     */
    public function debit(Money $amount): void
    {
        $this->balance = $this->balance->minus($amount);
    }

    public function status(): string
    {
        return $this->state->name();
    }

    public function balance(): Money
    {
        return $this->balance;
    }
}
