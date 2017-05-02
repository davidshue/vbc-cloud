package z9.cloud;

public interface Proxy {

    /**
     * @return the identifier
     */
    String getIdentifier();

    void startExecutor();

    void stopExecutor();

    boolean isRunning();
}