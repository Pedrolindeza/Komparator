package org.komparator.mediator.ws;

public class AliveThread extends Thread {

	MediatorPortImpl _portImpl;
	private Object result;
	private Exception exception;

    public AliveThread(MediatorPortImpl portImpl) {
        _portImpl = portImpl;
    }

    public Object getResult() {
        synchronized(this) {
            return this.result;
        }
    }

    public Exception getException() {
        synchronized(this) {
            return this.exception;
        }
    }

    public void run() {
        try {
            System.out.println(this.getClass() + " running...");

            // Do something...
            // (in this example, sleep)
            
            _portImpl.imAlive();

            // Acquire lock and notify waiting thread
            synchronized(this) {
                this.notifyAll();
            }

        } catch (Exception e) {
            System.out.println(this.getClass() + " caught exception: " + e.toString());
            this.exception = e;

        } finally {
            System.out.println(this.getClass() + " stopping.");
        }
    }

}
