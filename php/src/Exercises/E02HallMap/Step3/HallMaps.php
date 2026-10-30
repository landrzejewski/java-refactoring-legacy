<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step3;

use Training\Workshop\Exercises\E02HallMap\HallSpec;

/** Krok 3: klient czyta nazwę i ikonę z kategorii miejsca - wynik identyczny jak w start. */
final class HallMaps
{
    public function describe(HallSpec $spec): string
    {
        $hall = Hall::of($spec);

        $counts = [];
        $seats = [];
        foreach ($hall as $seat) {
            $counts[$seat->category()->name()] = ($counts[$seat->category()->name()] ?? 0) + 1;
            $seats[] = $seat->label() . ' ' . $seat->category()->icon();
        }

        $categories = [];
        foreach ($counts as $name => $count) {
            $categories[] = $name . '=' . $count;
        }
        return 'Sala ' . $hall->name() . ': pojemnosc ' . $hall->capacity() . ', doplaty ' . $hall->surcharges() . "\n"
            . 'Kategorie: ' . implode(' ', $categories) . "\n"
            . 'Miejsca: ' . implode(', ', $seats);
    }
}
