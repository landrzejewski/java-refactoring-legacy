<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E09CashDrawer\DrawerLine;

/** Start: jedyne miejsce tworzenia wpisów - tłumaczy surową linię dziennika na wpis szuflady. */
final class DrawerEntries
{
    private function __construct()
    {
    }

    public static function from(DrawerLine $line): DrawerEntry
    {
        return match ($line->kind) {
            'CASH' => new CashSale($line->reference, $line->amount),
            'CARD' => new CardSale($line->reference, $line->amount, $line->note),
            'REFUND' => new Refund($line->reference, $line->amount, $line->note),
            'DEPOSIT' => new Deposit($line->amount, $line->note),
            default => throw new InvalidArgumentException('unknown entry kind: ' . $line->kind),
        };
    }
}
