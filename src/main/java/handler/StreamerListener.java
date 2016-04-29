package handler;

/**
 * Created by G0DZ on 24.04.2016.
 */
public interface StreamerListener {
    public void onClientConnectedIn();
    public void onClientDisconnected();
    public void onException(Throwable t);
}
