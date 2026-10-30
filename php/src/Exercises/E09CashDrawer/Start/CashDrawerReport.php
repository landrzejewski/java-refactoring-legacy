<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E09CashDrawer\DrawerDay;
use Training\Workshop\Shared\Money;

/**
 * Start: raport zamknięcia kasy. Trzy operacje na wpisach szuflady (wpływ na gotówkę, linia
 * eksportu fiskalnego, linia audytu) to trzy łańcuchy instanceof zakończone wyjątkiem w runtime:
 * nowy rodzaj wpisu ładuje się bez błędu i wybucha przy zamknięciu dnia. Podział rabatu
 * grupowego na bilety wybiera łańcuch if po nazwie trybu z konfiguracji, wymieszany z regułą
 * "10+ biletów = -10%" i z poprawką zaokrąglenia, która musi dać dokładnie kwotę rabatu.
 */
final class CashDrawerReport
{
    private const int GROUP_SIZE = 10;
    private const int GROUP_PERCENT = 10;
    private const string CENT = '0.01';

    public function render(DrawerDay $day): string
    {
        $entries = array_map(DrawerEntries::from(...), $day->lines);
        $cash = Money::zero();
        $fiscal = '';
        $audit = '';
        foreach ($entries as $entry) {
            $cash = $cash->plus($this->cashEffect($entry));
            $fiscalLine = $this->fiscalLine($entry);
            if ($fiscalLine !== '') {
                $fiscal .= $fiscalLine . "\n";
            }
            $audit .= $this->auditLine($entry) . "\n";
        }
        return 'Gotowka w szufladzie: ' . $cash . "\n"
            . "Eksport fiskalny:\n" . $fiscal
            . "Audyt:\n" . $audit
            . $this->groupDiscount($day->groupTickets, $day->splitMode) . "\n";
    }

    private function cashEffect(DrawerEntry $entry): Money
    {
        if ($entry instanceof CashSale) {
            return $entry->amount;
        } elseif ($entry instanceof CardSale) {
            return Money::zero();
        } elseif ($entry instanceof Refund) {
            return Money::zero()->minus($entry->amount);
        } elseif ($entry instanceof Deposit) {
            return $entry->amount;
        }
        throw new InvalidArgumentException('unknown entry: ' . get_debug_type($entry));
    }

    /** Format drukarki fiskalnej: rodzaj;numer;kwota;stawka VAT[;karta]. Pogotowie kasowe nie jest fiskalne. */
    private function fiscalLine(DrawerEntry $entry): string
    {
        if ($entry instanceof CashSale) {
            return 'S;' . $entry->reference . ';' . $entry->amount . ';8';
        } elseif ($entry instanceof CardSale) {
            return 'K;' . $entry->reference . ';' . $entry->amount . ';8;' . $entry->cardLast4;
        } elseif ($entry instanceof Refund) {
            return 'Z;' . $entry->reference . ';-' . $entry->amount . ';8';
        } elseif ($entry instanceof Deposit) {
            return '';
        }
        throw new InvalidArgumentException('unknown entry: ' . get_debug_type($entry));
    }

    private function auditLine(DrawerEntry $entry): string
    {
        if ($entry instanceof CashSale) {
            return 'CASH ' . $entry->reference . ' ' . $entry->amount;
        } elseif ($entry instanceof CardSale) {
            return 'CARD ' . $entry->reference . ' ' . $entry->amount . ' karta ****' . $entry->cardLast4;
        } elseif ($entry instanceof Refund) {
            return 'REFUND ' . $entry->reference . ' -' . $entry->amount . ' powod: ' . $entry->reason;
        } elseif ($entry instanceof Deposit) {
            return 'DEPOSIT ' . $entry->amount . ' ' . $entry->note;
        }
        throw new InvalidArgumentException('unknown entry: ' . get_debug_type($entry));
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
