package pl.training.patterns.behavioral.observer;

class ServerEvent {
    private String payload;

    ServerEvent(final String payload) {
        this.payload = payload;
    }


    public static class ServerEventBuilder {
        private String payload;

        ServerEventBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public ServerEvent.ServerEventBuilder payload(final String payload) {
            this.payload = payload;
            return this;
        }

        public ServerEvent build() {
            return new ServerEvent(this.payload);
        }

        @Override
        public String toString() {
            return "ServerEvent.ServerEventBuilder(payload=" + this.payload + ")";
        }
    }

    public static ServerEvent.ServerEventBuilder builder() {
        return new ServerEvent.ServerEventBuilder();
    }

    public String getPayload() {
        return this.payload;
    }
}
