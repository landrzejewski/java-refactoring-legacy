<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E03Notifications;

use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionNamedType;
use ReflectionProperty;
use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;
use Training\Workshop\Exercises\E03Notifications\Step2\ChangeStats;
use Training\Workshop\Exercises\E03Notifications\Step2\Channel;
use Training\Workshop\Exercises\E03Notifications\Step2\CustomerNotifier;
use Training\Workshop\Exercises\E03Notifications\Step2\EmailChannel;
use Training\Workshop\Exercises\E03Notifications\Step2\EmailLink;
use Training\Workshop\Exercises\E03Notifications\Step2\Journal;
use Training\Workshop\Exercises\E03Notifications\Step2\PushChannel;
use Training\Workshop\Exercises\E03Notifications\Step2\PushLink;
use Training\Workshop\Exercises\E03Notifications\Step2\ScreeningCancelled;
use Training\Workshop\Exercises\E03Notifications\Step2\ScreeningMoved;
use Training\Workshop\Exercises\E03Notifications\Step2\ScreeningScheduler;
use Training\Workshop\Exercises\E03Notifications\Step2\SmsLink;
use Training\Workshop\Exercises\E03Notifications\Step2\UndeliveredLink;
use Training\Workshop\Exercises\E03Notifications\Step2\Waitlist;

/**
 * Kontrakt rozwiazania: nadawca zna tylko interfejs odbiorcow, nowy kanal dziala z kazdym rodzajem wiadomosci,
 * a ogniwo dostarczania przekazuje dalej tylko to, czego samo nie obsluzylo.
 *
 * Różnica względem Javy: {@see Channel} nie jest interfejsem funkcyjnym, więc lambdy z testów Javy
 * są tu klasami anonimowymi z publicznym licznikiem albo listą.
 */
final class E03SolutionTest extends TestCase
{
    #[Test]
    public function linkPassesOnOnlyWhatItCannotDeliver(): void
    {
        $journal = new Journal();
        $spy = new class implements Channel {
            public int $passedOn = 0;

            public function deliver(Customer $customer, string $subject, string $body): void
            {
                $this->passedOn++;
            }
        };
        $push = new PushLink(new PushChannel($journal), $spy);

        $push->deliver(self::anna(), 'tytul', 'tresc');
        self::assertSame(['PUSH tok-anna | tytul | tresc'], $journal->lines());
        self::assertSame(0, $spy->passedOn, 'obsluzone zadanie nie idzie dalej');

        $push->deliver(self::celina(), 'tytul', 'tresc');
        self::assertSame(1, $spy->passedOn, 'nieobsluzone zadanie idzie dokladnie do nastepnika');
        self::assertCount(1, $journal->lines(), 'ogniwo bez kanalu nic nie wysyla');
    }

    #[Test]
    public function chainIsAssembledExplicitlySoAFestivalCanUseEmailOnly(): void
    {
        $journal = new Journal();
        $emailOnly = new EmailLink(new EmailChannel($journal), new UndeliveredLink($journal));
        (new CustomerNotifier($emailOnly))->screeningMoved(new ScreeningMoved(self::diuna(), self::moved(), [self::anna()]));
        self::assertSame(['EMAIL anna@kino.pl | Zmiana seansu: Diuna | Seans Diuna z 2026-10-30 18:00 '
            . 'przeniesiony na 2026-10-31 20:00, sala 2. Bilety zachowuja waznosc.'], $journal->lines());
    }

    #[Test]
    public function everyLinkKnowsOnlyItsSuccessor(): void
    {
        foreach ([PushLink::class, SmsLink::class, EmailLink::class] as $link) {
            self::assertSame(1, self::fieldsOfType($link, Channel::class), (new ReflectionClass($link))->getShortName());
        }
        self::assertSame(0, self::fieldsOfType(UndeliveredLink::class, Channel::class), 'koniec lancucha nie ma nastepnika');
    }

    #[Test]
    public function newChannelIsOneClassWorkingWithEveryKindOfNotice(): void
    {
        $fax = new class implements Channel {
            /** @var list<string> */
            public array $faxes = [];

            public function deliver(Customer $customer, string $subject, string $body): void
            {
                $this->faxes[] = 'FAX ' . $customer->name . ' | ' . $subject;
            }
        };

        $notifier = new CustomerNotifier($fax);

        $notifier->screeningMoved(new ScreeningMoved(self::diuna(), self::moved(), [self::anna()]));
        $notifier->screeningCancelled(new ScreeningCancelled(self::diuna(), 'awaria projektora', [self::celina()]));

        self::assertSame(['FAX Anna | Zmiana seansu: Diuna', 'FAX Celina | Odwolany seans: Diuna'], $fax->faxes);
    }

    #[Test]
    public function schedulerKnowsOnlyTheListenerInterface(): void
    {
        $concreteListeners = [CustomerNotifier::class, Waitlist::class, ChangeStats::class];
        $leaks = [];
        foreach (self::fieldTypes(ScreeningScheduler::class) as $type) {
            if (in_array($type, $concreteListeners, true)) {
                $leaks[] = (new ReflectionClass($type))->getShortName();
            }
        }
        self::assertSame([], $leaks, 'scheduler nie trzyma konkretnych odbiorcow');
    }

    #[Test]
    public function unsubscribedListenerIsNotNotified(): void
    {
        $journal = new Journal();
        $scheduler = new ScreeningScheduler($journal);
        $stats = new ChangeStats($journal);
        $scheduler->subscribe($stats);
        $scheduler->unsubscribe($stats);
        $scheduler->register(self::diuna());

        $scheduler->move('S1', '2026-10-31 20:00', '2', [self::anna()]);

        self::assertSame(['SCHEDULE Diuna 2026-10-30 18:00 -> 2026-10-31 20:00 sala 2'], $journal->lines());
        foreach ($journal->lines() as $line) {
            self::assertFalse(str_starts_with($line, 'STATS'));
        }
    }

    private static function diuna(): Screening
    {
        return new Screening('S1', 'Diuna', '2026-10-30 18:00', '1');
    }

    private static function moved(): Screening
    {
        return new Screening('S1', 'Diuna', '2026-10-31 20:00', '2');
    }

    private static function anna(): Customer
    {
        return new Customer('Anna', 'anna@kino.pl', '600100200', 'tok-anna');
    }

    private static function celina(): Customer
    {
        return new Customer('Celina', 'celina@kino.pl', null, null);
    }

    /** @return list<string> */
    private static function fieldTypes(string $class): array
    {
        return array_map(
            fn (ReflectionProperty $property) => $property->getType() instanceof ReflectionNamedType
                ? $property->getType()->getName()
                : (string) $property->getType(),
            (new ReflectionClass($class))->getProperties(),
        );
    }

    private static function fieldsOfType(string $class, string $type): int
    {
        return count(array_filter(self::fieldTypes($class), fn (string $field) => $field === $type));
    }
}
