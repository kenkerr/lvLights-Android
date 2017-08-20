package com.kennethiankerr.lvlightremote.util;

        import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import net.wimpi.modbus.*;
        import net.wimpi.modbus.msg.*;
        import net.wimpi.modbus.io.*;
        import net.wimpi.modbus.net.*;
        import net.wimpi.modbus.procimg.SimpleRegister;
        import net.wimpi.modbus.util.*;

/**
 * Description
 *
 * @author Catalin Prata
 *         Date: 2/12/13
 *
 *         2015.09.13 Ken Kerr  Modified for LightController
 */
public class TcpClient {

    public static final int SERVER_PORT = 502;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    private Socket socket;
    private static final String TAG = "TCP Client";

    TCPMasterConnection             con     = null; //the connection
    ModbusTCPTransaction            trans   = null; //the transaction
    WriteSingleRegisterRequest      wrtReq  = null;
    WriteSingleRegisterResponse     wrtResp = null;
    SimpleRegister                  reg     = new SimpleRegister();

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
            Log.i(TAG,"message sent");
        } else {
            Log.e(TAG, "message not sent ");
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        Log.i("Debug", "stopClient");

        // send message that we are closing the connection
        //sendMessage(Constants.CLOSED_CONNECTION + "Kazy");

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(String serverIP) {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            Log.i("TCP Client", "C: Connecting to " + serverAddr.toString());

            con = new TCPMasterConnection(serverAddr);
            con.setPort(SERVER_PORT);
            con.connect();

            wrtReq = new WriteSingleRegisterRequest();
            wrtReq.setReference(0);                         // HR[0]
            reg.setValue(1);
            wrtReq.setRegister(reg);

            trans = new ModbusTCPTransaction(con);
            trans.setRequest(wrtReq);

            Log.i("TCP Client", "transaction ready...");

            trans.execute();

            Log.i("TCP Client", "transaction executed");
/*
            //create a socket to make the connection with the server
            socket = new Socket(serverAddr, SERVER_PORT);

            try {
                Log.i("TCP Client", "inside try catch");
                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
*/
                /*
                while (mRun) {
                    mServerMessage = mBufferIn.readLine();
                    if (mServerMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(mServerMessage);
                    }
                }
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");
                */
/*
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                Log.i("TCP Client", "...ready for action");
//                socket.close();
            }
*/
        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}