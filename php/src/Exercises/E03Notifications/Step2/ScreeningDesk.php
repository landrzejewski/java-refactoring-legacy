<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

use InvalidArgumentException;
use Training\Workshop\Exercises\E03Notifications\Cancel;
use Training\Workshop\Exercises\E03Notifications\Move;
use Training\Workshop\Exercises\E03Notifications\Scenario;

/**
 * Krok 2: korzen kompozycji sklada obie struktury w jednym miejscu: kolejnosc odbiorcow zdarzen
 * (powiadomienia, lista, statystyki) i kolejnosc kanalow dostarczania (push, SMS, e-mail, niedostarczone).
 * Inna polityka kanalow (np. tylko e-mail dla festiwalu) to inny lancuch, bez zmian w pozostalych klasach.
 */
final class ScreeningDesk
{
    public function run(Scenario $scenario): string
    {
        $journal = new Journal();
        $scheduler = new ScreeningScheduler($journal);
        $scheduler->subscribe(new CustomerNotifier(self::standardDelivery($journal)));
        $scheduler->subscribe(new Waitlist($journal, $scenario->waiting));
        $scheduler->subscribe(new ChangeStats($journal));
        $scheduler->register($scenario->screening);
        $id = $scenario->screening->id;
        try {
            foreach ($scenario->changes as $change) {
                match (true) {
                    $change instanceof Move => $scheduler->move($id, $change->newStart, $change->newHall, $scenario->ticketHolders),
                    $change instanceof Cancel => $scheduler->cancel($id, $change->reason, $scenario->ticketHolders),
                };
            }
        } catch (InvalidArgumentException $exception) {
            $journal->note('ERROR: ' . $exception->getMessage());
        }
        return implode("\n", $journal->lines());
    }

    /** Standardowa sciezka dostarczania kina: push, potem SMS, potem e-mail, na koncu wpis o niedostarczeniu. */
    public static function standardDelivery(Journal $journal): Channel
    {
        return new PushLink(
            new PushChannel($journal),
            new SmsLink(
                new SmsChannel($journal),
                new EmailLink(
                    new EmailChannel($journal),
                    new UndeliveredLink($journal),
                ),
            ),
        );
    }
}
