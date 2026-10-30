<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step1;

use InvalidArgumentException;
use Training\Workshop\Exercises\E03Notifications\Cancel;
use Training\Workshop\Exercises\E03Notifications\Move;
use Training\Workshop\Exercises\E03Notifications\Scenario;

/**
 * Krok 1: korzen kompozycji rejestruje standardowy zestaw odbiorcow w kolejnosci, ktora byla
 * zaszyta w schedulerze: powiadomienia klientow, lista oczekujacych, statystyki.
 */
final class ScreeningDesk
{
    public function run(Scenario $scenario): string
    {
        $journal = new Journal();
        $scheduler = new ScreeningScheduler($journal);
        $scheduler->subscribe(new CustomerNotifier($journal));
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
}
