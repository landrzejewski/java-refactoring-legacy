package pl.training.patterns.behavioral.visitor;

public class SuperDepartment extends Department {

    public SuperDepartment(String name) {
        super(name);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
        children.forEach(node -> node.accept(visitor));
    }

}
