package pl.training.module4.behavioral.visitor;

public interface Visitor {

    default  void visit(Department department) {
    }

    default  void visit(SuperDepartment department) {
    }

    default void visit(Employee employee) {
    }

}
