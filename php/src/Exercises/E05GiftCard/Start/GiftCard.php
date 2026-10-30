<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Start;

use InvalidArgumentException;
use LogicException;
use Training\Workshop\Shared\Money;

/**
 * Start: karta podarunkowa ze statusem w polu tekstowym (ISSUED, ACTIVE, EMPTY, BLOCKED, EXPIRED).
 * Każda operacja zaczyna się od switch po statusie, więc tabela przejść jest rozproszona po
 * czterech metodach: nowy status (np. zawieszona) albo nowa operacja to zmiana w każdej z nich,
 * a komunikat "card is blocked" powtarza się w trzech miejscach.
 */
final class GiftCard
{
    private string $status = 'ISSUED';
    private Money $balance;

    public function __construct(Money $initialBalance)
    {
        if (!$initialBalance->isGreaterThan(Money::zero())) {
            throw new InvalidArgumentException('initial balance must be positive');
        }
        $this->balance = $initialBalance;
    }

    public function activate(): void
    {
        switch ($this->status) {
            case 'ISSUED':
                $this->status = 'ACTIVE';
                break;
            case 'ACTIVE':
                throw new LogicException('card already active');
            case 'EMPTY':
                throw new LogicException('card is empty');
            case 'BLOCKED':
                throw new LogicException('card is blocked');
            case 'EXPIRED':
                throw new LogicException('card has expired');
            default:
                throw new LogicException('unknown status: ' . $this->status);
        }
    }

    public function charge(Money $amount): void
    {
        if (!$amount->isGreaterThan(Money::zero())) {
            throw new InvalidArgumentException('amount must be positive');
        }
        switch ($this->status) {
            case 'ISSUED':
                throw new LogicException('card not activated');
            case 'ACTIVE':
                if ($amount->isGreaterThan($this->balance)) {
                    throw new LogicException('insufficient balance');
                }
                $this->balance = $this->balance->minus($amount);
                if ($this->balance->compareTo(Money::zero()) === 0) {
                    $this->status = 'EMPTY';
                }
                break;
            case 'EMPTY':
                throw new LogicException('card is empty');
            case 'BLOCKED':
                throw new LogicException('card is blocked');
            case 'EXPIRED':
                throw new LogicException('card has expired');
            default:
                throw new LogicException('unknown status: ' . $this->status);
        }
    }

    public function block(): void
    {
        switch ($this->status) {
            case 'ISSUED':
            case 'ACTIVE':
                $this->status = 'BLOCKED';
                break;
            case 'EMPTY':
                throw new LogicException('card is empty');
            case 'BLOCKED':
                throw new LogicException('card is blocked');
            case 'EXPIRED':
                throw new LogicException('card has expired');
            default:
                throw new LogicException('unknown status: ' . $this->status);
        }
    }

    public function expire(): void
    {
        switch ($this->status) {
            case 'ISSUED':
            case 'ACTIVE':
            case 'EMPTY':
            case 'BLOCKED':
                $this->status = 'EXPIRED';
                break;
            case 'EXPIRED':
                throw new LogicException('card has expired');
            default:
                throw new LogicException('unknown status: ' . $this->status);
        }
    }

    public function status(): string
    {
        return $this->status;
    }

    public function balance(): Money
    {
        return $this->balance;
    }
}
