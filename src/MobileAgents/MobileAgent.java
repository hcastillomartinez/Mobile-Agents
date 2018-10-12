package MobileAgents;

import java.util.concurrent.BlockingQueue;

public class MobileAgent implements SensorObject, Runnable {
    
    BlockingQueue<String> messages;
    
    /**
     *
     */
    public MobileAgent(BlockingQueue<String> messages) {
        this.messages = messages;
    }


    /**
     * Creating a new agent if the node below is heated.
     * @return alertAgent
     */
    protected MobileAgent clone() {
        return new MobileAgent(this.messages);
    }

    
    /**
     *
     */
    @Override
    public void sendMessage() {
    
    }
    
    @Override
    public void getMessage() {
    
    }

    @Override
    public void run() {
    
    }
}
