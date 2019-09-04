package com.springroad.airmousesampleclientsocket;

import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class AirMouseLocalServerSocketExcutorService {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ArrayList<AirMouseLocalMessageReceiveThread> serverThreads = new ArrayList<AirMouseLocalMessageReceiveThread>();
    private  static  String TAG = "[AirMouseApp_Server]";

    public void stopServerSocket(){

        try{

            serverSocket.close();

        }catch (IOException e)
        {
            Log.d(TAG,  e.getMessage());
        }

    }

    public void runServerSocket(){

        try{
            String ipaddress = null;
            ipaddress = getLocalIpAddress();

            Log.d(TAG,  "Server IP Address : " + ipaddress);
            if(getLocalIpAddress() != null )
            {
                serverSocket = new ServerSocket(Const.AIRMOUSE_LOCAL_PORT);
            }

        }catch (IOException e){
            Log.d(TAG,  e.getMessage());
        }

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    if(getLocalIpAddress() != null )
                    {
                        Log.d(TAG,  "Sever is running continue  !!!!");

                        try{
                            clientSocket = serverSocket.accept();
                        }catch (IOException e){
                            Log.d(TAG,  e.getMessage());
                        }

                        Log.d(TAG,  "Client Socket was accepted !!!!!!!!");

                        AirMouseLocalMessageReceiveThread messageReceiveThread = new AirMouseLocalMessageReceiveThread(clientSocket);
                        messageReceiveThread.start();
                        /*
                        try {
                            //Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        */
                    }

                }
            }
        });
        th.start();

    }

    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& inetAddress instanceof Inet4Address) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

}
