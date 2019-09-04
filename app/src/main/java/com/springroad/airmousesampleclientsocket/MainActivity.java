package com.springroad.airmousesampleclientsocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private  static  String TAG = "[AirMouseApp_Client]";

    Button bt_connect;
    Button bt_move_mouse_point;
    Button bt_start_server;
    Button bt_stop_server;

    EditText et_ip;
    EditText et_port;

    private  String ip ="192.168.0.16";
    private static int port = Const.AIRMOUSE_LOCAL_PORT;

    LinkedList<SocketClient> threadList;
    Socket socket;
    SocketClient socketClient;
    SendThread sendThread;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_start_server = (Button)findViewById(R.id.bt_start_server);
        bt_stop_server = (Button)findViewById(R.id.bt_stop_server);
        bt_connect = (Button)findViewById(R.id.bt_connect);
        bt_move_mouse_point = (Button)findViewById(R.id.bt_move_mouse_point);
        et_ip =(EditText)findViewById(R.id.et_ip);
        et_port =(EditText)findViewById(R.id.et_port);

        ip = getLocalIpAddress();
        et_ip.setText(ip);
        et_port.setText(String.valueOf(port));

        threadList = new LinkedList<SocketClient>();


        bt_start_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startServer();

            }
        });


        bt_stop_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopServer();

            }
        });

        bt_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sport= et_port.getText().toString();
                String ip = et_ip.getText().toString();

                if(sport != null && sport!="")
                {
                    socketClient = new SocketClient(ip,Integer.parseInt(sport));
                    threadList.add(socketClient);
                    socketClient.start();
                }
            }
        });

        bt_move_mouse_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sendMessage;
                sendMessage = getMessageForMousePoint();

                sendMessage = sendMessage + "\n";
                Log.v(TAG, "send Message : " + sendMessage);
                sendThread = new SendThread(socket,sendMessage);
                sendThread.start();
            }
        });

    }

    public AirMouseLocalServerSocketExcutorService airMouseLocalServerSocketExcutorService;

    public void startServer(){

        airMouseLocalServerSocketExcutorService = new AirMouseLocalServerSocketExcutorService();
        airMouseLocalServerSocketExcutorService.runServerSocket();

    }


    public void stopServer(){

        airMouseLocalServerSocketExcutorService = new AirMouseLocalServerSocketExcutorService();
        airMouseLocalServerSocketExcutorService.stopServerSocket();

    }

    public String getMessageForMousePoint(){
        String strMessage  = "";
        try{
            JSONObject jsonObjectRoot = new JSONObject();
            JSONObject jsonObjectCommand = new JSONObject();

            // 1. command
            jsonObjectCommand.put("type","REQUEST");
            jsonObjectCommand.put("action","Dpad_airmouse_x_y");
            jsonObjectCommand.put("service","DPAD_AIRMOUSE");

                // 1.1 bundle
                JSONArray jsonBundleArray = new JSONArray();
                    JSONObject jsonObjectBunddleX= new JSONObject();
                    jsonObjectBunddleX.put("X", 100 );
                    JSONObject jsonObjectBunddleY= new JSONObject();
                    jsonObjectBunddleY.put("Y", 100 );
                jsonBundleArray.put(jsonObjectBunddleX);
                jsonBundleArray.put(jsonObjectBunddleY);

            jsonObjectCommand.put("bundle",jsonBundleArray);

            //2. root
            jsonObjectRoot.put("command",jsonObjectCommand);
            jsonObjectRoot.put("date","");
            jsonObjectRoot.put("receiverId","");
            jsonObjectRoot.put("senderId","");

            strMessage = jsonObjectRoot.toString();

        }catch (JSONException je){
            Log.v(TAG,je.getMessage());
        }


        return strMessage;
    }
    class SocketClient extends  Thread{

        boolean threadAlive;
        String ip;
        int port;
        String mac;

        OutputStream outputStream = null;
        BufferedReader br = null;

        private DataOutputStream output = null;

        public SocketClient(String ip, int port){
            threadAlive = true;
            this.ip = ip;
            this.port = port;


            Log.v(TAG, "Create SocketClient() !!!! : ip : " + this.ip + " port : " + this.port );

        }

        @Override
        public void run() {

          try{

              Log.v("[AirMouseApp]", "Client Socket is running!!!!");

              socket = new Socket(ip,port);

              Log.v("[AirMouseApp]", "Socket was conneted !!!!");

          }catch (IOException e){

          }
        }
    }

    class SendThread extends Thread{

        private Socket socket;
        String sendmsg= "";
        DataOutputStream outputStream;

        public SendThread(Socket socket,String sendmsg){

           this.sendmsg= sendmsg;

            this.socket = socket;
            try{
                outputStream = new DataOutputStream(socket.getOutputStream());
            }catch (Exception e){

            }
        }

        @Override
        public void run() {
            try{
                //outputStream.writeUTF(sendmsg);
                outputStream.writeBytes(sendmsg);
                Log.v("[AirMouseApp]", "Message was sended : " +  sendmsg );

            }catch (IOException e){
                Log.v("[AirMouseApp]",e.getMessage());
            }catch (NullPointerException ne){
                Log.v("[AirMouseApp]",ne.getMessage());

            }
        }
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
            Log.e("[AirMouseApp]", ex.toString());
        }
        return null;
    }

}
