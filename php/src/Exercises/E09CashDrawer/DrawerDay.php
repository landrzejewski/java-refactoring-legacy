<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer;

use Training\Workshop\Shared\Money;

/**
 * Stabilny kontrakt ćwiczenia: dzień kasowy do zamknięcia. Wpisy szuflady oraz ceny biletów
 * jednej sprzedaży grupowej (po zniżkach indywidualnych, mogą mieć dowolne grosze)
 * i tryb podziału rabatu grupowego z konfiguracji kina (PROPORTIONAL, LAST, EQUAL).
 */
final readonly class DrawerDay
{
    /**
     * @param list<DrawerLine> $lines
     * @param list<Money> $groupTickets
     */
    public function __construct(public array $lines, public array $groupTickets, public string $splitMode)
    {
    }
}
