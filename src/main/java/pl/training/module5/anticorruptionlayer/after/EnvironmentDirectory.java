package pl.training.module5.anticorruptionlayer.after;

import java.util.List;

/**
 * Port domenowy. Domena nie wie, że po drugiej stronie jest CMDB.
 */
@FunctionalInterface
public interface EnvironmentDirectory {
    List<Environment> all();
}
