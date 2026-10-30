<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

use InvalidArgumentException;
use LogicException;
use Training\Workshop\Exercises\E04SeatPicker\HallPlan;
use Training\Workshop\Exercises\E04SeatPicker\Seat;

/**
 * Krok 2: wywołujący - tłumaczy linię skryptu na obiekt polecenia, wykonuje je i odkłada na stos.
 * UNDO zdejmuje ostatnie polecenie i prosi je o cofnięcie; drugi łańcuch if zniknął, a każda
 * linia skryptu dostaje własny obiekt z własnymi danymi do cofnięcia.
 */
final class SeatPicker
{
    private readonly Cart $cart;
    /** @var list<Command> stos: ostatnie polecenie na końcu */
    private array $history = [];

    public function __construct(HallPlan $plan)
    {
        $this->cart = new Cart($plan);
    }

    public function apply(string $line): void
    {
        $parts = preg_split('/\s+/', trim($line));
        $op = strtoupper($parts[0]);
        if ($op === 'UNDO') {
            $this->undo();
            return;
        }
        $command = $this->parse($op, $parts);
        $command->execute();
        $this->history[] = $command;
    }

    /** @param list<string> $parts */
    private function parse(string $op, array $parts): Command
    {
        return match ($op) {
            'SELECT' => new SelectSeat($this->cart, Seat::parse(self::arg($parts, 1))),
            'DESELECT' => new DeselectSeat($this->cart, Seat::parse(self::arg($parts, 1))),
            'SWAP' => new SwapSeats($this->cart, Seat::parse(self::arg($parts, 1)), Seat::parse(self::arg($parts, 2))),
            'GLASSES' => new SetGlasses($this->cart, self::parseGlasses(self::arg($parts, 1))),
            'VOUCHER' => new SetVoucher($this->cart, self::parseVoucher(self::arg($parts, 1))),
            'CLEAR' => new ClearSeats($this->cart),
            'AUTO' => new AutoArrange($this->cart, self::parseCount(self::arg($parts, 1))),
            default => throw new InvalidArgumentException('nieznana operacja: ' . $parts[0]),
        };
    }

    private function undo(): void
    {
        if ($this->history === []) {
            throw new LogicException('nie ma czego cofnac');
        }
        array_pop($this->history)->undo();
    }

    public function state(): string
    {
        return $this->cart->state();
    }

    /** @param list<string> $parts */
    private static function arg(array $parts, int $index): string
    {
        if (count($parts) <= $index) {
            throw new InvalidArgumentException('brak argumentu operacji ' . strtoupper($parts[0]));
        }
        return $parts[$index];
    }

    private static function parseGlasses(string $text): bool
    {
        return match (strtolower($text)) {
            'own' => true,
            'rent' => false,
            default => throw new InvalidArgumentException('okulary: own albo rent'),
        };
    }

    private static function parseVoucher(string $text): ?string
    {
        if (strcasecmp($text, 'none') === 0) {
            return null;
        }
        if ($text === 'KINO10') {
            return $text;
        }
        throw new InvalidArgumentException('nieznany voucher: ' . $text);
    }

    private static function parseCount(string $text): int
    {
        if (preg_match('/^[+-]?\d+$/', $text) !== 1 || (int) $text < -2147483648 || (int) $text > 2147483647) {
            throw new InvalidArgumentException('zla liczba miejsc: ' . $text);
        }
        return (int) $text;
    }
}
