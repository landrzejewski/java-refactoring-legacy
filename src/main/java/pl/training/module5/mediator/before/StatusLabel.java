package pl.training.module5.mediator.before;

public final class StatusLabel {
    private String text = "enter version";

    public String text() {
        return text;
    }

    void setText(String text) {
        this.text = text;
    }
}
