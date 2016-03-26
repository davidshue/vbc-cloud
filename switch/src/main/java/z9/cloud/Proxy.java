package z9.cloud;

public interface Proxy {

    /**
     * @return the identifier
     */
    public String getIdentifier();

    public void startExecutor();

    public void stopExecutor();
    
    public boolean isRunning();
}