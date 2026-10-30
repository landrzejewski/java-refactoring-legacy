<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Exercises\E09CashDrawer\DrawerDay;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: raport składa się z trzech odwiedzających i reguły rabatu grupowego z wariantem
 * podziału wybranym po nazwie w DiscountSplits. Nowy rodzaj wpisu = zmiana każdego
 * odwiedzającego (PHP nie załaduje klasy bez nowej metody interfejsu), nowa operacja albo
 * nowy tryb podziału = jedna klasa.
 * Uwaga: wybór wariantu zapada przed sprawdzeniem wielkości grupy, więc nieznany tryb
 * zgłasza się teraz także dla grupy poniżej 10 biletów - świadoma zmiana opisana w teście.
 */
final class CashDrawerReport
{
    private readonly CashEffectVisitor $cashEffect;
    private readonly FiscalLineVisitor $fiscalLine;
    private readonly AuditLineVisitor $auditLine;

    public function __construct()
    {
        $this->cashEffect = new CashEffectVisitor();
        $this->fiscalLine = new FiscalLineVisitor();
        $this->auditLine = new AuditLineVisitor();
    }

    public function render(DrawerDay $day): string
    {
        $entries = array_map(DrawerEntries::from(...), $day->lines);
        $cash = Money::zero();
        $fiscal = '';
        $audit = '';
        foreach ($entries as $entry) {
            $cash = $cash->plus($entry->accept($this->cashEffect));
            $line = $entry->accept($this->fiscalLine);
            if ($line !== '') {
                $fiscal .= $line . "\n";
            }
            $audit .= $entry->accept($this->auditLine) . "\n";
        }
        return 'Gotowka w szufladzie: ' . $cash . "\n"
            . "Eksport fiskalny:\n" . $fiscal
            . "Audyt:\n" . $audit
            . $this->groupDiscount($day->groupTickets, $day->splitMode) . "\n";
    }

    /** @param list<Money> $tickets */
    private function groupDiscount(array $tickets, string $mode): string
    {
        $rule = new GroupDiscount(DiscountSplits::forMode($mode));
        $allocation = $rule->allocate($tickets);
        return $allocation === null
            ? 'Rabat grupowy: brak (' . count($tickets) . ' biletow)'
            : 'Rabat grupowy ' . $mode . ': ' . $allocation->discount . ' = ' . implode(' + ', $allocation->shares);
    }
}
