
package mmqa.fetcher;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingClientConnectionManager;

public class IdleConnectionMonitorThread extends Thread {
    
    private final PoolingClientConnectionManager connMgr;
    private volatile boolean shutdown;
    
    public IdleConnectionMonitorThread(PoolingClientConnectionManager connMgr) {
        super("Connection Manager");
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000);
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 30 sec
                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }
    
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
    
}

