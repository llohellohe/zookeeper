package yangqi.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by yangqi on 2/1/14.
 */
public class ZkNIOClient {
    private Selector selector = null;

    private SelectionKey selectionKey = null;

    private SocketChannel socketChannel;

    public static void main(String[] args) {
        ZkNIOClient client = new ZkNIOClient();

        int port=2181;

        String host="127.0.0.1";

        System.out.println("start to connect to "+host+" :" + port);

        InetSocketAddress address=new InetSocketAddress(host,port);

        boolean connected = client.connect(address);

        System.out.println("start to connect to "+host+" :" + port+",status "+connected);

        int i=0;
        while(i++<3){
            client.listen();
        }
    }

    private boolean connect(SocketAddress socketAddress) {
        socketChannel = createSocket();

        try {
            selectionKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);
            return socketChannel.connect(socketAddress);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }

    private SocketChannel createSocket() {
        try {
            selector = Selector.open();

            SocketChannel socketChannel = SocketChannel.open();

            socketChannel.socket().setTcpNoDelay(false);
            socketChannel.socket().setSoLinger(false, -1);
            socketChannel.configureBlocking(false);

            return socketChannel;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void listen(){
        try {
            int keys= selector.select();

            for (SelectionKey key:selector.selectedKeys()){
                System.out.println("Selection key is "+key);

                if(key.isConnectable()){
                    System.out.println("Connecting ");

                    SocketChannel channel = (SocketChannel) key.channel();

                    if(channel.isConnectionPending()){
                        channel.finishConnect();
                    }

                    channel.write(ByteBuffer.wrap(new String("stat").getBytes()));

                    channel.register(selector,SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    System.out.println("Reading data ");

                    SocketChannel channel=(SocketChannel)key.channel();
                    ByteBuffer buffer=ByteBuffer.allocate(1024);
                    channel.read(buffer);

                    System.out.println("read zk server stat "+new String(buffer.array()));

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
