<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step2;

use InvalidArgumentException;
use Training\Workshop\Exercises\E02HallMap\RowSpec;
use Training\Workshop\Shared\Money;

/** Krok 2: rząd - węzeł nad miejscami; luka nadal nie tworzy dziecka, ale zużywa numer. */
final class Row extends HallGroup
{
    /** @param list<Seat> $seats */
    private function __construct(private readonly string $label, array $seats)
    {
        parent::__construct($seats);
    }

    public static function of(RowSpec $spec): self
    {
        $layout = $spec->layout;
        $seats = [];
        for ($i = 0; $i < strlen($layout); $i++) {
            $code = $layout[$i];
            if ($code === '_') {
                continue;
            }
            $seats[] = self::seatFor($code, $spec->label . ($i + 1));
        }
        if ($seats === []) {
            throw new InvalidArgumentException('row ' . $spec->label . ' has no seats');
        }
        return new self($spec->label, $seats);
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
}
