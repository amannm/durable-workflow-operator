package dev.restate.sdk.endpoint;

public class Endpoint {
    private final Object service;

    private Endpoint(Object service) {
        this.service = service;
    }

    public static Endpoint bind(Object service) {
        return new Endpoint(service);
    }

    public Endpoint build() {
        return this;
    }

    public Object getService() {
        return service;
    }
}
