<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step1;

use InvalidArgumentException;
use Training\Workshop\Exercises\E09CashDrawer\DrawerDay;
use Training\Workshop\Shared\Money;

/**
 * Krok 1: trzy operacje na wpisach to trzy odwiedzający; raport tylko sumuje i skleja ich wyniki.
 * W klasie nie ma już instanceof. Podział rabatu grupowego nadal łańcuchem if po trybie.
 */
final class CashDrawerReport
{
    private const int GROUP_SIZE = 10;
    private const int GROUP_PERCENT = 10;
    private const string CENT = '0.01';

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
        if (count($tickets) < self::GROUP_SIZE) {
            return 'Rabat grupowy: brak (' . count($tickets) . ' biletow)';
        }
        $sum = Money::zero();
        foreach ($tickets as $ticket) {
            $sum = $sum->plus($ticket);
        }
        $discount = $sum->percent(self::GROUP_PERCENT);
        $shares = [];
        if ($mode === 'PROPORTIONAL') {
            $given = Money::zero();
            for ($i = 0; $i < count($tickets) - 1; $i++) {
                $share = new Money($discount->amount->mul($tickets[$i]->amount)->div($sum->amount, 10));
                $shares[] = $share;
                $given = $given->plus($share);
            }
            $shares[] = $discount->minus($given);
        } elseif ($mode === 'LAST') {
            for ($i = 0; $i < count($tickets) - 1; $i++) {
                $shares[] = Money::zero();
            }
            $shares[] = $discount;
        } elseif ($mode === 'EQUAL') {
            $base = new Money($discount->amount->div(count($tickets), 2));
            $restCents = (int) (string) $discount->minus($base->times(count($tickets)))->amount->mul(100);
            for ($i = 0; $i < count($tickets); $i++) {
                $shares[] = $i < $restCents ? $base->plus(Money::of(self::CENT)) : $base;
            }
        } else {
            throw new InvalidArgumentException('unknown split mode: ' . $mode);
        }
        return 'Rabat grupowy ' . $mode . ': ' . $discount . ' = ' . implode(' + ', $shares);
    }
}
