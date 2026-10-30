<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E10Showtime;

use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionMethod;
use ReflectionNamedType;
use ReflectionProperty;
use Training\Workshop\Exercises\E10Showtime\Step2\AutoScheduler;
use Training\Workshop\Exercises\E10Showtime\Step2\BoxOfficePanel;
use Training\Workshop\Exercises\E10Showtime\Step2\Showtime;

/**
 * Krok 2: urządzenia znają tylko sterownik, kolejność reakcji jest w jednym miejscu,
 * a klienci znają tylko fasadę; podsystemy nie są publiczne.
 */
final class E10SolutionTest extends TestCase
{
    private const string STEP2 = 'Training\\Workshop\\Exercises\\E10Showtime\\Step2\\';
    private const array DEVICES = ['Lights', 'Sound', 'Doors', 'AdPlayer', 'Projector'];
    private const array SUBSYSTEM = ['TicketScanner', 'AttendanceReport', 'HallController',
        'HallMediator', 'DeviceLog', 'Lights', 'Sound', 'Doors', 'AdPlayer', 'Projector'];

    #[Test]
    public function noDeviceHoldsAnotherDevice(): void
    {
        $allowed = ['HallMediator', 'DeviceLog', 'int', 'bool'];
        foreach (self::DEVICES as $device) {
            foreach ((new ReflectionClass(self::STEP2 . $device))->getProperties() as $property) {
                if ($property->isStatic()) {
                    continue;
                }
                self::assertContains(self::shortTypeName($property), $allowed, $device . ' zna ' . self::shortTypeName($property));
            }
        }
    }

    #[Test]
    public function onlyTheHallControllerKnowsTheDevices(): void
    {
        $owners = [];
        foreach (['HallController', 'TicketScanner', 'AttendanceReport', 'Showtime',
            'BoxOfficePanel', 'AutoScheduler', 'ShowtimeRunner'] as $name) {
            $holdsDevice = array_any(
                (new ReflectionClass(self::STEP2 . $name))->getProperties(),
                fn (ReflectionProperty $property) => in_array(self::shortTypeName($property), self::DEVICES, true),
            );
            if ($holdsDevice) {
                $owners[] = $name;
            }
        }
        self::assertSame(['HallController'], $owners, 'kolejność reakcji urządzeń w jednym miejscu');
    }

    #[Test]
    public function clientsKnowOnlyTheFacade(): void
    {
        foreach ([BoxOfficePanel::class, AutoScheduler::class] as $client) {
            $dependencies = array_values(array_map(
                fn (ReflectionProperty $property) => (string) $property->getType(),
                array_filter((new ReflectionClass($client))->getProperties(), fn (ReflectionProperty $property) => !$property->isStatic()),
            ));
            self::assertSame([Showtime::class], $dependencies, $client);
        }
    }

    #[Test]
    public function subsystemsAreHiddenBehindTheFacade(): void
    {
        // Różnica względem Javy: PHP nie ma widoczności pakietowej - podsystemy i konstruktor fasady
        // są oznaczone w PHPDoc jako @internal i to właśnie sprawdzamy zamiast modyfikatora dostępu.
        foreach (self::SUBSYSTEM as $name) {
            self::assertStringContainsString('@internal', (string) (new ReflectionClass(self::STEP2 . $name))->getDocComment(), $name);
        }
        self::assertStringContainsString(
            '@internal',
            (string) (new ReflectionMethod(Showtime::class, '__construct'))->getDocComment(),
            'fasadę składa korzeń kompozycji',
        );
    }

    private static function shortTypeName(ReflectionProperty $property): string
    {
        $type = $property->getType();
        if (!$type instanceof ReflectionNamedType) {
            return '';
        }
        $name = $type->getName();
        $separator = strrpos($name, '\\');
        return $separator === false ? $name : substr($name, $separator + 1);
    }
}
