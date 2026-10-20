package pl.training.workshop.m5.s31_anticorruptionlayer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import pl.training.workshop.m5.s31_anticorruptionlayer.step3.DistributorAcl;
import pl.training.workshop.m5.s31_anticorruptionlayer.step3.FilmLicense;
import pl.training.workshop.m5.s31_anticorruptionlayer.step3.SettlementService;
import pl.training.workshop.shared.Money;

/** Typy dystrybutora nie przekraczają ACL; licencja w języku kina pilnuje swoich niezmienników. */
final class S31SolutionTest {
    private static final String LEGACY_PACKAGE = "pl.training.workshop.m5.s31_anticorruptionlayer";

    @Test
    void domainServiceUsesNoLegacyType() {
        for (Class<?> type : signatureTypes(SettlementService.class)) {
            assertFalse(type.getPackageName().equals(LEGACY_PACKAGE), "typ dystrybutora w domenie: " + type);
        }
    }

    @Test
    void translatorIsAnImplementationDetailOfTheAcl() throws ClassNotFoundException {
        Class<?> translator = Class.forName(LEGACY_PACKAGE + ".step3.DistributorTranslator");
        assertFalse(Modifier.isPublic(translator.getModifiers()));
    }

    @Test
    void aclTranslatesLegacyConventions() {
        var acl = new DistributorAcl(new DistributorFeed(List.of(
                new DistributorRecord("KRL", "KRAINA LODU", "S", "20261015", "20261115", "30000", " 40 %"))));
        assertEquals(Optional.of(new FilmLicense("KRL", "Kraina lodu", false, LocalDate.parse("2026-10-15"),
                LocalDate.parse("2026-11-15"), 40, Money.of("300.00"))), acl.find("KRL"));
        assertEquals(Optional.empty(), acl.find("XYZ"), "null z systemu dystrybutora nie wychodzi poza ACL");
    }

    @Test
    void aclRejectsMalformedLegacyDataAtTheBoundary() {
        var acl = new DistributorAcl(new DistributorFeed(List.of(
                new DistributorRecord("DIU", "DIUNA", "A", "2026-10-01", "20261031", "50000", "45%"))));
        var error = assertThrows(IllegalArgumentException.class, () -> acl.find("DIU"));
        assertEquals("invalid legacy date: 2026-10-01", error.getMessage());
    }

    @Test
    void licenseProtectsItsInvariants() {
        LocalDate day = LocalDate.parse("2026-10-20");
        assertThrows(IllegalArgumentException.class,
                () -> new FilmLicense("DIU", "Diuna", true, day, day.minusDays(1), 45, Money.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> new FilmLicense("DIU", "Diuna", true, day, day, 120, Money.ZERO));
    }

    @Test
    void serviceWorksWithAnyLicenseSource() {
        LocalDate day = LocalDate.parse("2026-10-20");
        FilmLicense festival = new FilmLicense("FST", "Festiwal", true, day, day, 10, Money.of("50.00"));
        SettlementService service = new SettlementService(code -> Optional.of(festival));
        assertTrue(service.canScreen("FST", day));
        assertEquals("ROZLICZENIE Festiwal 2026-10-20: przychod 1000.00, udzial 10%, minimum 50.00, "
                + "dla dystrybutora 100.00", service.settle("FST", day, Money.of("1000.00")));
    }

    private static List<Class<?>> signatureTypes(Class<?> type) {
        List<Class<?>> types = new ArrayList<>();
        Arrays.stream(type.getDeclaredFields()).map(Field::getType).forEach(types::add);
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            types.addAll(List.of(constructor.getParameterTypes()));
        }
        for (Method method : type.getDeclaredMethods()) {
            types.addAll(List.of(method.getParameterTypes()));
            types.add(method.getReturnType());
        }
        return types;
    }
}
