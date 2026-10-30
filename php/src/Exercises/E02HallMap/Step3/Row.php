<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step3;

use InvalidArgumentException;
use Training\Workshop\Exercises\E02HallMap\RowSpec;

/**
 * Krok 3: rząd nie zna już danych kategorii - prosi o nią {@see SeatCategories} i dokłada
 * tylko to, co jest własne dla miejsca: etykietę z numerem.
 */
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
            $seats[] = new Seat($spec->label . ($i + 1), SeatCategories::of($code));
        }
        if ($seats === []) {
            throw new InvalidArgumentException('row ' . $spec->label . ' has no seats');
        }
        return new self($spec->label, $seats);
    }

    public function label(): string
    {
        return $this->label;
    }
}
