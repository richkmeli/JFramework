package it.richkmeli.jframework.network.udp;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class P2pChatARQTest {

    @Test
    public void chatExample() throws InterruptedException {

        // A
        P2pChatARQ peerA = new P2pChatARQ();
        peerA.startListen(6000);

        // B
        P2pChatARQ peerB = new P2pChatARQ();
        peerB.startListen(6001);


        Thread.sleep(400);
        // B
        InputStream inputStreamB = new ByteArrayInputStream("i'm peerB".getBytes());
        peerB.startTalk("127.0.0.1", 6000, null, inputStreamB, false);

        Thread.sleep(400);
        // A
        InputStream inputStreamA = new ByteArrayInputStream("i'm peerA".getBytes());
        peerA.startTalk("127.0.0.1", 6001, null, inputStreamA, false);

    }

}