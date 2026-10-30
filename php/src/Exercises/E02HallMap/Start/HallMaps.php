<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Start;

use Training\Workshop\Exercises\E02HallMap\HallSpec;
use Training\Workshop\Shared\Money;

/**
 * Start: klient mapy sali. Cztery razy wykonuje tę samą wędrówkę sala, sektor, rząd, miejsce
 * i za każdym razem sam pomija luki. Nowy poziom (balkon z własnymi sektorami) albo nowe
 * pytanie o salę to kolejna kopia potrójnej pętli.
 */
final class HallMaps
{
    public function describe(HallSpec $spec): string
    {
        $hall = Hall::of($spec);

        $capacity = 0;
        foreach ($hall->sections() as $section) {
            foreach ($section->rows() as $row) {
                foreach ($row->places() as $seat) {
                    if ($seat === null) {
                        continue;
                    }
                    $capacity++;
                }
            }
        }

        $surcharges = Money::zero();
        foreach ($hall->sections() as $section) {
            foreach ($section->rows() as $row) {
                foreach ($row->places() as $seat) {
                    if ($seat === null) {
                        continue;
                    }
                    $surcharges = $surcharges->plus($seat->surcharge());
                }
            }
        }

        $counts = [];
        foreach ($hall->sections() as $section) {
            foreach ($section->rows() as $row) {
                foreach ($row->places() as $seat) {
                    if ($seat === null) {
                        continue;
                    }
                    $counts[$seat->categoryName()] = ($counts[$seat->categoryName()] ?? 0) + 1;
                }
            }
        }

        $seats = [];
        foreach ($hall->sections() as $section) {
            foreach ($section->rows() as $row) {
                foreach ($row->places() as $seat) {
                    if ($seat === null) {
                        continue;
                    }
                    $seats[] = $seat->label() . ' ' . $seat->icon();
                }
            }
        }

        $categories = [];
        foreach ($counts as $name => $count) {
            $categories[] = $name . '=' . $count;
        }
        return 'Sala ' . $hall->name() . ': pojemnosc ' . $capacity . ', doplaty ' . $surcharges . "\n"
            . 'Kategorie: ' . implode(' ', $categories) . "\n"
            . 'Miejsca: ' . implode(', ', $seats);
    }
}
