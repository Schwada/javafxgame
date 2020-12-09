package com.schwada.mpgame.net;


import com.schwada.mpgame.logic.DataHandler;
import com.schwada.mpgame.logic.GameState;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Logger;

public class Client implements Runnable {

    private final static Logger logger = Logger.getLogger(Client.class.getName());
    private DatagramSocket socket;
    private InetAddress IPAddress;
    private DataHandler processor;

    private byte[] in = new byte[1024];
    private byte[] out = new byte[1024];

    private final int PORT = 8088;

    /**
     * Our Client constructor which instantiates our socket and
     * sets state for changes to be made
     * @param state current game state instance
     */
    public Client(GameState state, boolean serverMode) {
        try {
            IPAddress = InetAddress.getByName("127.0.0.1");
            if(serverMode) {
                socket = new DatagramSocket(PORT);
            }else {
                socket = new DatagramSocket();
            }
            processor = new DataHandler(state);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            logger.info("Client - error starting, socket problem or unknown host");
        }
    }

    /**
     * Creates a datagram packet which will allow us send our
     * message back to our datagram packet server
     * @param packet packet
     */
    public void sendPacketTo(String[] packet, InetAddress address, int port) {
        try {
            out = ByteUtil.toByteArray(packet);
            DatagramPacket releasePacket = new DatagramPacket(out, out.length, address, port);
            socket.send(releasePacket);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Client - could not send packet");
        }
    }

    public void sendPacket(String[] packet){
        this.sendPacketTo(packet, IPAddress, PORT);
    }

    @Override
    public void run() {
        logger.info("Client - started...");
        while (true) {
            try {
                DatagramPacket receivedPacket = new DatagramPacket(in, in.length);
                socket.receive(receivedPacket);
                String[] packetData = (String[]) ByteUtil.toObject(receivedPacket.getData());
                // TODO Fronta
                String[] processedData = processor.process(packetData, receivedPacket.getAddress().toString());
                if (processedData.length > 0) this.sendPacketTo(processedData, receivedPacket.getAddress(), receivedPacket.getPort());
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("Client - could not receive packet");
            }
        }
    }

    public void setServerIp(String IPAddress) {
        try {
            this.IPAddress = InetAddress.getByName(IPAddress);
        } catch (UnknownHostException e) {
            logger.info("Client - could not set server ip");
        }
    }

}
