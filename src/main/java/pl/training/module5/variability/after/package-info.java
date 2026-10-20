/**
 * Command, Strategy czy State? Te same {@code switch}-e z
 * {@code LegacyReleaseConsole} rozdzielone według tego, kto wybiera wariant i kiedy:
 * <ul>
 * <li><b>Strategy</b> ({@link pl.training.module5.variability.after.SupportCostPolicy}) -
 * wariant wybiera klient raz (np. przy tworzeniu obiektu) i nie zmienia go w trakcie życia
 * obiektu; rodzina algorytmów jest stabilna i wymienna.</li>
 * <li><b>Command</b> ({@link pl.training.module5.variability.after.OperatorCommand}) -
 * żądanie staje się obiektem, bo trzeba je kolejkować, logować i cofać; wariant wybiera
 * nadawca żądania przy każdym wywołaniu.</li>
 * <li><b>State</b> ({@link pl.training.module5.variability.after.ReleaseLifecycle}) -
 * nikt z zewnątrz nie wybiera wariantu; obiekt sam zmienia zachowanie, przechodząc
 * między stanami.</li>
 * </ul>
 */
package pl.training.module5.variability.after;
