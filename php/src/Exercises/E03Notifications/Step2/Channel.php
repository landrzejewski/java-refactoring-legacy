<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use Training\Workshop\Exercises\E03Notifications\Customer;

/**
 * Krok 2: kanal dostarczania (e-mail, SMS, push) i zarazem ogniwo lancucha dostarczania.
 * Zna tylko adresata, tytul i tresc; formatowanie pod ograniczenia kanalu jest po jego stronie.
 */
interface Channel
{
    public function deliver(Customer $customer, string $subject, string $body): void;
}
