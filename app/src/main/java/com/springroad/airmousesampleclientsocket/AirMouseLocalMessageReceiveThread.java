package com.springroad.airmousesampleclientsocket;

import android.util.Log;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class AirMouseLocalMessageReceiveThread extends Thread {

    private Socket socket;
    private DataInputStream dataInputStream;
    BufferedReader bufferedReader;

    private  static  String TAG = "[AirMouseApp_MessageReceive]";

    public AirMouseLocalMessageReceiveThread(Socket socket) {
        this.socket = socket;
        try{
            dataInputStream  = new DataInputStream(this.socket.getInputStream());
            //bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Log.d(TAG, "AirMouseLocalMessageReciveThread is created !!!!");

        }catch (IOException e)
        {
            Log.d(TAG, e.getMessage());
        }

    }
    public void run() {

        Log.d(TAG, "AirMouseLocalMessageReceiveThread is started !!!!");

        while (true) {
            try {
                /*
                String receiveMassage = dataInputStream.readUTF();
                if( receiveMassage == null || receiveMassage.isEmpty() ) {
                    Log.d(TAG, "Received Massage is empty !!!!");
                    break;
                }else{
                    Log.d(TAG, "Received message : " + receiveMassage);
                }
                */

                /*
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {

                    line.replaceAll("\r","");
                    line.replaceAll("\n","");
                    Log.d(TAG, "Received message : " + line);
                    break;
                }
                */
                StringBuffer buffer = new StringBuffer();
                int i;
                char c;
                String str = "";
                String full_str = "";
                byte[] readByte = new byte[1];
                while(dataInputStream.read(readByte) != -1){

                    Log.d(TAG, "Received message : " + readByte);

                    str = new String(readByte, "euc-kr");

                    if(str != "\n")
                    {
                        full_str = full_str + str;
                        Log.d(TAG, "full_str : " + full_str);

                    }else{
                        break;
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }


}
