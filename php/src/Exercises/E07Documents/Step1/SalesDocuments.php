<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

use InvalidArgumentException;
use Training\Workshop\Exercises\E07Documents\Channel;
use Training\Workshop\Exercises\E07Documents\PrintOptions;
use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Shared\Money;

/**
 * Krok 1: trzy switch-e zastąpione jednym wyborem rodziny. Opcjonalne sekcje nadal
 * są łańcuchem if z obliczeniami w środku - to zostaje na krok 2.
 */
final class SalesDocuments
{
    public function print(?Sale $sale, ?Channel $channel, ?PrintOptions $options): string
    {
        if ($sale === null) {
            throw new InvalidArgumentException('sale must not be null');
        }
        if ($channel === null) {
            throw new InvalidArgumentException('channel must not be null');
        }
        if ($options === null) {
            throw new InvalidArgumentException('options must not be null');
        }
        if ($sale->ticketPrice->compareTo(Money::zero()) < 0) {
            throw new InvalidArgumentException('ticket price must not be negative');
        }
        if ($sale->barPrice->compareTo(Money::zero()) < 0) {
            throw new InvalidArgumentException('bar price must not be negative');
        }
        $family = DocumentFactories::forChannel($channel);
        $receiptLayout = $family->receiptLayout();
        $document = (new DocumentComposer($family))->compose($sale);
        if ($options->withVat) {
            $lines = [];
            $lines[] = 'VAT 8% bilet: ' . self::vat($sale->ticketPrice, 8);
            if ($sale->barPrice->isGreaterThan(Money::zero())) {
                $lines[] = 'VAT 23% bar: ' . self::vat($sale->barPrice, 23);
            }
            $document .= "\n" . $receiptLayout->section($lines);
        }
        if ($options->withPromo) {
            $document .= "\n" . $receiptLayout->section(['Kod KINO10: -10% na nastepny bilet online']);
        }
        if ($options->withLoyaltyFooter) {
            $earned = intdiv((int) (string) $sale->ticketPrice->amount, 10);
            $total = $sale->loyaltyPoints + $earned;
            $lines = [];
            $lines[] = 'Punkty: ' . $sale->loyaltyPoints . ' + ' . $earned . ' = ' . $total;
            if ($total >= 100) {
                $lines[] = 'Odbierz darmowy bilet 2D';
            }
            $document .= "\n" . $receiptLayout->section($lines);
        }
        return $document;
    }

    /** VAT zawarty w cenie brutto, np. 8% z 40.00 to 2.96. */
    private static function vat(Money $gross, int $rate): Money
    {
        return new Money($gross->amount->mul($rate)->div(100 + $rate, 10));
    }
}
