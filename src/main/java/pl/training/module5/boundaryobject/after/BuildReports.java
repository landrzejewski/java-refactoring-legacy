package pl.training.module5.boundaryobject.after;

/**
 * Port domenowy: domena prosi o {@link BuildReport}, nie o surowy ładunek.
 */
@FunctionalInterface
public interface BuildReports {
    BuildReport find(String buildId);
}
