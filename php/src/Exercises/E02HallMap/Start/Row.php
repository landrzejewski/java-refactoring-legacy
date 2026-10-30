<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E02HallMap\RowSpec;
use Training\Workshop\Shared\Money;

/**
 * Start: rząd trzyma tablicę miejsc, w której przejście (luka) to {@code null}. Każdy, kto
 * przechodzi po rzędzie, musi o tym pamiętać. Dane kategorii (nazwa, dopłata, ikona, opis)
 * są kopiowane do każdego miejsca z osobna - w sali IMAX na 400 miejsc to 400 kopii.
 */
final class Row
{
    /** @param list<?Seat> $places */
    private function __construct(private readonly string $label, private readonly array $places)
    {
    }

    public static function of(RowSpec $spec): self
    {
        $layout = $spec->layout;
        $places = array_fill(0, strlen($layout), null);
        $seats = 0;
        for ($i = 0; $i < strlen($layout); $i++) {
            $code = $layout[$i];
            if ($code === '_') {
                continue;
            }
            $places[$i] = self::seatFor($code, $spec->label . ($i + 1));
            $seats++;
        }
        if ($seats === 0) {
            throw new InvalidArgumentException('row ' . $spec->label . ' has no seats');
        }
        return new self($spec->label, $places);
    }

    private static function seatFor(string $code, string $label): Seat
    {
        return match ($code) {
            'N' => new Seat($label, 'N', 'Normalne', Money::zero(), 'o', 'Miejsce standardowe'),
            'V' => new Seat($label, 'V', 'VIP', Money::of('10.00'), '*', 'Fotel skorzany z szerszym rozstawem'),
            'W' => new Seat($label, 'W', 'Wozek', Money::zero(), '&', 'Miejsce dla osoby na wozku'),
            'D' => new Seat($label, 'D', 'Dwuosobowe', Money::of('6.00'), '=', 'Sofa dla dwojga'),
            default => throw new InvalidArgumentException('unknown seat category: ' . $code),
        };
    }

    public function label(): string
    {
        return $this->label;
    }

    /**
     * Tablica z lukami: {@code null} oznacza przejście.
     *
     * @return list<?Seat>
     */
    public function places(): array
    {
        return $this->places;
    }
}
