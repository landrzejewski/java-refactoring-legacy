<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step2;

use Training\Workshop\Exercises\E02HallMap\HallSpec;

/**
 * Krok 2: klient nie zna już poziomów sali. Agregaty liczy drzewo, a po miejscach
 * przechodzi zwykłą pętlą foreach - kolejność i pomijanie luk należą do sali.
 */
final class HallMaps
{
    public function describe(HallSpec $spec): string
    {
        $hall = Hall::of($spec);

        $counts = [];
        $seats = [];
        foreach ($hall as $seat) {
            $counts[$seat->categoryName()] = ($counts[$seat->categoryName()] ?? 0) + 1;
            $seats[] = $seat->label() . ' ' . $seat->icon();
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
