package protocol.cache;

import protocol.messages.MessagePong;

public class CachePong extends Cache<String, MessagePong> {

    public CachePong(
            long maxCapacity) {

        super(maxCapacity);
    }

    public void init() {

        //refresh cache thread
    }
}