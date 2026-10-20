package pl.training.workshop.m2.s03_compilergate;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Test równoważności: usuwanie ostrzeżeń kompilatora nie zmienia raportu obłożenia. */
final class S03EquivalenceTest {
    record Input(int format, List<String> seats) {
    }

    @TestFactory
    Stream<DynamicTest> everyStepDescribesOccupancyTheSameWay() {
        return Scene.<Input, String>variants()
                .variant("start", in -> {
                    var map = new pl.training.workshop.m2.s03_compilergate.start.SeatMap();
                    in.seats().forEach(map::take);
                    return new pl.training.workshop.m2.s03_compilergate.start.OccupancyReport().describe(map, in.format());
                })
                .variant("step1", in -> {
                    var map = new pl.training.workshop.m2.s03_compilergate.step1.SeatMap();
                    in.seats().forEach(map::take);
                    return new pl.training.workshop.m2.s03_compilergate.step1.OccupancyReport().describe(map, in.format());
                })
                .variant("step2", in -> {
                    var map = new pl.training.workshop.m2.s03_compilergate.step2.SeatMap();
                    in.seats().forEach(map::take);
                    return new pl.training.workshop.m2.s03_compilergate.step2.OccupancyReport().describe(map, in.format());
                })
                .variant("step3", in -> {
                    var map = new pl.training.workshop.m2.s03_compilergate.step3.SeatMap();
                    in.seats().forEach(map::take);
                    return new pl.training.workshop.m2.s03_compilergate.step3.OccupancyReport().describe(map, in.format());
                })
                .expect("IMAX - przelot do Dolby", new Input(3, List.of("A1", "B1", "C10")),
                        "IMAX [duzy ekran, dzwiek Dolby], cena 40 zl, zajete: {1=2, 10=1}")
                .expect("3D", new Input(2, List.of("D5")), "3D [dzwiek Dolby], cena 32 zl, zajete: {5=1}")
                .expect("2D, pusta sala", new Input(1, List.of()), "2D [standard], cena 25 zl, zajete: {}")
                .tests();
    }
}
