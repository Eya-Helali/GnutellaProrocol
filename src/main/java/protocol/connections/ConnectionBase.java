package protocol.connections;

import protocol.messages.MessageBase;

import java.util.concurrent.BlockingQueue;

public abstract class ConnectionBase implements Connection {

    private BlockingQueue<MessageBase> rcvMessageQueue;

    protected ConnectionBase(
            BlockingQueue<MessageBase> rcvMessageQueue) {

        this.rcvMessageQueue = rcvMessageQueue;
    }

    public BlockingQueue<MessageBase> getRcvMessageQueue() {
        return this.rcvMessageQueue;
    }
}