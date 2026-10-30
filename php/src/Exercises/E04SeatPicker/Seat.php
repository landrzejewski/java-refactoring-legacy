<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker;

use InvalidArgumentException;
use Stringable;

/**
 * Stabilny kontrakt sceny: miejsce w sali zapisywane jako "rzad-numer", np. 6-4.
 *
 * Różnica względem Javy: równość rekordu to w PHP porównanie {@code ==} (te same pola), dlatego
 * listy miejsc są przeszukiwane przez {@code in_array}/{@code array_search} bez trybu ścisłego.
 */
final readonly class Seat implements Stringable
{
    public function __construct(public int $row, public int $number)
    {
    }

    public static function parse(string $text): self
    {
        // Różnica względem Javy: String.split pomija puste elementy na końcu, a Integer.parseInt
        // przyjmuje znak +/- i tylko wartości z zakresu int - tu odtworzone ręcznie.
        $parts = explode('-', $text);
        while ($parts !== [] && end($parts) === '') {
            array_pop($parts);
        }
        if (count($parts) !== 2) {
            throw new InvalidArgumentException('zly format miejsca: ' . $text);
        }
        $row = self::parseInt($parts[0]);
        $number = self::parseInt($parts[1]);
        if ($row === null || $number === null) {
            throw new InvalidArgumentException('zly format miejsca: ' . $text);
        }
        return new self($row, $number);
    }

    private static function parseInt(string $text): ?int
    {
        if (preg_match('/^[+-]?\d+$/', $text) !== 1) {
            return null;
        }
        $value = (int) $text;
        return $value >= -2147483648 && $value <= 2147483647 ? $value : null;
    }

    public function __toString(): string
    {
        return $this->row . '-' . $this->number;
    }
}
