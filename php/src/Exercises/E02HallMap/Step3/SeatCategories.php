<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step3;

use InvalidArgumentException;
use Training\Workshop\Shared\Money;

/**
 * Krok 3: fabryka współdzielonych kategorii. Pierwsze pytanie o kod tworzy kategorię,
 * każde kolejne zwraca tę samą instancję. Nieznany kod daje ten sam błąd co w start.
 *
 * Różnica względem Javy: zwykła tablica statyczna zamiast {@code ConcurrentHashMap},
 * bo żądanie PHP wykonuje się w jednym wątku.
 */
final class SeatCategories
{
    /** @var array<string, SeatCategory> */
    private static array $shared = [];

    private function __construct()
    {
    }

    public static function of(string $code): SeatCategory
    {
        return self::$shared[$code] ??= self::create($code);
    }

    /**
     * Wszystkie dotąd utworzone kategorie - do podglądu, ile naprawdę istnieje instancji.
     *
     * @return list<SeatCategory>
     */
    public static function known(): array
    {
        return array_values(self::$shared);
    }

    private static function create(string $code): SeatCategory
    {
        return match ($code) {
            'N' => new SeatCategory('N', 'Normalne', Money::zero(), 'o', 'Miejsce standardowe'),
            'V' => new SeatCategory('V', 'VIP', Money::of('10.00'), '*', 'Fotel skorzany z szerszym rozstawem'),
            'W' => new SeatCategory('W', 'Wozek', Money::zero(), '&', 'Miejsce dla osoby na wozku'),
            'D' => new SeatCategory('D', 'Dwuosobowe', Money::of('6.00'), '=', 'Sofa dla dwojga'),
            default => throw new InvalidArgumentException('unknown seat category: ' . $code),
        };
    }
}
