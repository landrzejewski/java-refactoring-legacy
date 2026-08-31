package pl.training.patterns.behavioral.templatemethod;

import java.util.List;

public class Application {

    static class UpperCaseProcessor extends Processor<List<String>, String> {

        @Override
        protected List<String> read() {
            return List.of("template", "method");
        }

        @Override
        protected String process(List<String> data) {
            return String.join(" ", data).toUpperCase();
        }

        @Override
        protected void write(String data) {
            System.out.println(data);
        }

    }

    public static void main(String[] args) {
        new UpperCaseProcessor().run();
    }

}
