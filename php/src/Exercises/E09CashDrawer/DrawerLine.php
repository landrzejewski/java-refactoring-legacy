<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer;

use Training\Workshop\Shared\Money;

/**
 * Stabilny kontrakt ćwiczenia: surowy wpis dziennika szuflady z systemu kasowego.
 * Rodzaj: CASH (sprzedaż gotówką), CARD (sprzedaż kartą, note = 4 ostatnie cyfry karty),
 * REFUND (zwrot gotówki, note = powód), DEPOSIT (pogotowie kasowe, note = opis).
 */
final readonly class DrawerLine
{
    public function __construct(public string $kind, public string $reference, public Money $amount, public string $note)
    {
    }
}
