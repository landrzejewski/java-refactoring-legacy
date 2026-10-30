<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step1;

use Training\Workshop\Exercises\E02HallMap\HallSpec;

/**
 * Krok 1: pojemność i dopłaty liczy drzewo. Zliczanie kategorii i lista miejsc nadal
 * wędrują po strukturze ręcznie - klient wciąż zna poziomy sala, sektor, rząd.
 */
final class HallMaps
{
    public function describe(HallSpec $spec): string
    {
        $hall = Hall::of($spec);

        $counts = [];
        foreach ($hall->sections() as $section) {
            foreach ($section->rows() as $row) {
                foreach ($row->seats() as $seat) {
                    $counts[$seat->categoryName()] = ($counts[$seat->categoryName()] ?? 0) + 1;
                }
            }
        }

        $seats = [];
        foreach ($hall->sections() as $section) {
            foreach ($section->rows() as $row) {
                foreach ($row->seats() as $seat) {
                    $seats[] = $seat->label() . ' ' . $seat->icon();
                }
            }
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
