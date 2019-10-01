//package protocol.peers;
//
//import protocol.cache.Cache;
//import protocol.cache.CachePong;
//import protocol.connections.TCPConnectionHandler;
//import protocol.exceptions.InvalidMessageTypeException;
//import protocol.messages.MessageBase;
//import protocol.messages.MessagePing;
//
//public class OldPeerStandardCachedPong extends PeerStandard {
//
//    private TCPConnectionHandler tcpConnectionHandler;
//    private Cache cachePong;
//
//    private final String localIp;
//    private final int localPort;
//
//    private Thread connectionHandlerThread;
//
//    public OldPeerStandardCachedPong(
//            int peerId,
//            int maxConnections,
//            int cacheDimension,
//            String localIp,
//            int localPort) {
//
//        super(peerId,maxConnections);
//
//        this.cachePong = new CachePong(cacheDimension);
//        this.localIp = localIp;
//        this.localPort = localPort;
//
//        this.tcpConnectionHandler = new TCPConnectionHandler(
//                getMessageQueue(),
//                localIp,
//                localPort,
//                maxConnections);
//    }
//
//    @Override
//    public void run() {
//        while(!Thread.currentThread().isInterrupted()) {
//            try {
//                MessageBase m = getMessageQueue().take();
//                switch(m.getType()) {
//
//                    //PING message
//                    case 0:
//
//
//                        break;
//
//                    //PONG message
//                    case 1:
//
//
//                        break;
//
//                    default:
//
//                        break;
//                }
//
//            }catch(Exception e) {
//
//                //
//            }
//        }
//
//
//    }
//
//    public String getLocalIp() {
//        return this.localIp;
//    }
//
//    public int getLocalPort() {
//        return this.localPort;
//    }
//
//    public TCPConnectionHandler getTcpConnectionHandler() { return this.tcpConnectionHandler; }
//
//    @Override
//    public synchronized void shutdown() {
//        tcpConnectionHandler.shutdown();
//        Thread.currentThread().interrupt();
//    }
//
//    @Override
//    public synchronized void init() {
//
//        tcpConnectionHandler.init();
//        connectionHandlerThread = new Thread(tcpConnectionHandler);
//        connectionHandlerThread.setName("PeerStandard "+getPeerId()+" ConnectionBase Handler Thread");
//        connectionHandlerThread.start();
//    }
//
//    @Override
//    public synchronized boolean connect(String ip, int port) {
//        return tcpConnectionHandler.connect(ip,port);
//    }
//
//    @Override
//    protected void receivePing(MessageBase msg) throws InvalidMessageTypeException {
//
//        if(msg instanceof MessagePing) {
//
//            MessagePing ping = (MessagePing) msg;
//
//            System.out.println("PeerStandard " +
//                    this.getPeerId() +
//                    "received a PING from PeerStandard " +
//                    msg.getSenderId() +
//                    " [GUID: " + msg.getGuid() +
//                    " | FLAG: " + msg.getType() +
//                    " | TTL: " + msg.getTtl() +
//                    " | HOPS: " + msg.getHops() + "]\n"
//            );
//
//            //check pong cache
//            if(!this.cachePong.isExpired()) {
//
//                //sendAllExceptSender all cache entries to ping sender
//                //connectionHandler
//            }
//
//        }
//        else throw new InvalidMessageTypeException("Invalid Ping MessageBase");
//
//
//
//    }
//
//    @Override
//    public void receivePong(MessageBase msg) {
//
//
//    }
//}