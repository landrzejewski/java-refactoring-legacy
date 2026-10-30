<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use InvalidArgumentException;
use Training\Workshop\Exercises\E07Documents\Channel;
use Training\Workshop\Exercises\E07Documents\PrintOptions;
use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: punkt wejścia zna tylko walidację, wybór rodziny i składanie dokumentu.
 * Nie ma tu ani jednego switch-a po kanale ani obliczeń żadnej sekcji.
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
        return (new DocumentComposer(DocumentFactories::forChannel($channel)))->compose($sale, $options)->render();
    }
}
