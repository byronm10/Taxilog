package com.example.nuevotaxilog;

import android.os.StrictMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class UDPSender {
    private Thread thread;
    private int port;
    private ArrayList <InetAddress> ips;

    public UDPSender(int port, ArrayList <String> ipAddr) throws IOException {


        this.port = port;
        this.ips = new ArrayList<>();


        for (String Direccion: ipAddr) {
            try {

                this.ips.add(InetAddress.getByName(Direccion));


            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        this.thread = null;
    }

    public void send(String message) {

        this.thread = new Thread() {
            public void run() {




                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8 );
                {
                    for ( InetAddress ip:  ips ) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        DatagramPacket dp= null;
                        if (ip != null )

                            dp = new DatagramPacket(message.getBytes(), message.length(), ip, port);

                        try {
                            DatagramSocket socket = new DatagramSocket();
                            if (dp != null)
                                socket.send(dp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }


                }



            }
        };
        this.thread.start();

    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ArrayList <InetAddress> getIps() {
        return ips;
    }


}