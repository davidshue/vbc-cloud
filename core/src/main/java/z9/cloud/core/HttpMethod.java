package z9.cloud.core;

public enum HttpMethod {
    GET,
    HEAD,
    TRACE,
    OPTIONS,
    CONNECT,
    PUT(true),
    POST(true),
    DELETE(true),
    PATCH(true);
    
    private boolean updateable = false;
    
    public static HttpMethod[] UPDATES = {PUT, POST, DELETE, PATCH};
    
    HttpMethod() {}
    
    HttpMethod(boolean updateable) {
        this.updateable = updateable;
    }
    
    /**
     * @return the updateable
     */
    public boolean isUpdateable() {
        return updateable;
    }
}
