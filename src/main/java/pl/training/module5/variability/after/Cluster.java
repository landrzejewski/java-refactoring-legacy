package pl.training.module5.variability.after;

/**
 * Odbiorca poleceń operatora.
 */
public final class Cluster {
    private int instances = 2;
    private boolean maintenance;

    public int instances() {
        return instances;
    }

    public boolean maintenance() {
        return maintenance;
    }

    void addInstance() {
        instances++;
    }

    void removeInstance() {
        if (instances == 1) {
            throw new IllegalStateException("cannot scale below one instance");
        }
        instances--;
    }

    void toggleMaintenance() {
        maintenance = !maintenance;
    }

    @Override
    public String toString() {
        return "instances=" + instances + ", maintenance=" + (maintenance ? "on" : "off");
    }
}
