package protocol.config;

public final class ProtocolConfig {

    /**
     * Payload Descriptors
     */
    public static final int PAYLOAD_DESCRIPTOR_PING = 0;
    public static final int PAYLOAD_DESCRIPTOR_PONG = 1;
    public static final int PAYLOAD_DESCRIPTOR_WELCOME = 2;
    public static final int PAYLOAD_DESCRIPTOR_OK = 3;

    /**
     * TTL
     */
    public static final int TTL_DEFAULT = 7;

    /**
     * Hops
     */
    public static final int HOPS_DEFAULT = 0;

    /**
     * Max neighbors to redirect
     */
    public static final int MAX_REDIRECTS_NEIGHBORS = 4;
}