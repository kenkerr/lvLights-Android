package com.kennethiankerr.lvlightremote;

import android.os.StrictMode;
import android.util.Log;

import com.kennethiankerr.lvlightremote.util.modbusRegister;

import net.wimpi.modbus.*;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.io.*;
import net.wimpi.modbus.net.*;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;
import net.wimpi.modbus.util.*;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Ken on 9/14/2015
 */
public class ControllerComm {

    private int state;
    private static final String     TAG = "ControllerComm";
    public static final int SERVER_PORT = 502;
    private static final int    MAX_HRS = 17;

    private  TCPMasterConnection                  con     = null; //the connection
    private  ModbusTCPTransaction                 trans   = null; //the transaction
    private  ReadMultipleRegistersRequest        readReq  = null;
    private  ReadMultipleRegistersResponse       readResp = null;
    private  WriteMultipleRegistersRequest        wrtReq  = null;
    private  WriteMultipleRegistersResponse       wrtResp = null;
    private  WriteSingleRegisterRequest     singleWrtReq  = null;
    private  WriteSingleRegisterResponse    singleWrtResp = null;
    private  SimpleRegister                       reg     = new SimpleRegister();

    /* Constructor */
    public ControllerComm () {
    }

    public void connect (String serverIP) {


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            InetAddress serverAddr = InetAddress.getByName(serverIP);
            Log.i(TAG, "C: Connecting to: " + serverAddr.toString());

            con = new TCPMasterConnection(serverAddr);
            con.setPort(SERVER_PORT);
            con.connect();

            if (null == con) {
                Log.e (TAG, "connection con not instantiated correctly");
            }

        } catch (Exception e) {
            Log.e (TAG, "TCP Error", e);
        }
    }
    public void close () {
        try {
            Log.i(TAG, "C: Closing connection");
            con.close();
        } catch (Exception e) {
            Log.e (TAG, "TCP Error", e);
        }
    }

    public void getRegisters (ArrayList<Integer> registers) {
        SimpleRegister[] regArray = new SimpleRegister[MAX_HRS];

        for (int i=0; i<MAX_HRS-1; i++) {
            regArray[i] = new SimpleRegister();
        }
        readReq = new ReadMultipleRegistersRequest (0, MAX_HRS);

        trans = new ModbusTCPTransaction(con);
        trans.setRequest(readReq);

        Log.i(TAG, "RMR transaction ready ...");

        try {
            trans.execute();
            readResp = (ReadMultipleRegistersResponse) trans.getResponse();
        } catch (Exception e) {
            Log.e (TAG, "Execute error", e);
        }

        registers.clear();                              // remove all elements from arraylist
        for (int i=0; i<MAX_HRS; i++) {
            registers.add(readResp.getRegisterValue(i));
        }

        Log.i(TAG, "transaction executed");
    }

      public void setControllerRegisters (int startingRegisterNumber, SimpleRegister[] regArray){

        Log.i(TAG, "sendRequest> sending registers["+startingRegisterNumber+"...] - " + regArray[0].getValue());

        wrtReq = new WriteMultipleRegistersRequest(startingRegisterNumber, regArray);

        trans = new ModbusTCPTransaction(con);
        trans.setRequest(wrtReq);

        Log.i("TCP Client", "WMR transaction ready, writing to HR["+startingRegisterNumber+"]");

        try {
            trans.execute();
            wrtResp = (WriteMultipleRegistersResponse) trans.getResponse();
        } catch (Exception e) {
            Log.e (TAG, "Execute error", e);
        }

        Log.i("TCP Client", "transaction executed");

    }

    public void sendRequest (int request) {
        switch (request) {
            case MainMenu.MANUAL_OFF:
            case MainMenu.MANUAL_ON:
            case MainMenu.MANUAL_BLINK:
                Log.i(TAG, "sendRequest> sending manual request " + request);
                SimpleRegister[] regArray = new SimpleRegister[2];

                regArray[0] = new SimpleRegister();
                regArray[0].setValue((int) 1);                        // HR[0] = 1 (Manual mode)

                regArray[1] = new SimpleRegister();
                regArray[1].setValue(request);                  // HR[1] = request {ON, OFF, BLINK}
                Log.i(TAG, "sendRequest> sending registers - " + regArray[0].getValue() + " " + regArray[1].getValue());

                int hr = 0;                                     // HR[0] (mode) is first reg to write
                wrtReq = new WriteMultipleRegistersRequest(hr, regArray);
/*
                wrtReq.setReference(0);                         // Starting reg = HR[0]
                wrtReq.setRegisters(regArray);
*/

                trans = new ModbusTCPTransaction(con);
                trans.setRequest(wrtReq);

                Log.i("TCP Client", "WMR transaction ready ...");

                try {
                    trans.execute();
                    wrtResp = (WriteMultipleRegistersResponse) trans.getResponse();
                } catch (Exception e) {
                    Log.e (TAG, "Execute error", e);
                }

                Log.i("TCP Client", "transaction executed");

                break;

            case MainMenu.MODE_MANUAL:
            case MainMenu.MODE_ABS_ON_OFF:
            case MainMenu.MODE_DUSK_TO_DAWN:
            case MainMenu.MODE_DUSK_TO_OFFSET:

                request -= 10;                              // MODE requests are offset by 10 so as not to conflict with MANUAL REQUESTS

                Log.i(TAG, "sendRequest> sending mode request " + request);

                regArray    = new SimpleRegister[1];
                regArray[0] = new SimpleRegister();
                regArray[0].setValue(request);

                Log.i(TAG, "sendRequest> sending registers - " + regArray[0].getValue());

                hr = 0;                                     // HR[0] (mode) is first reg to write
                wrtReq = new WriteMultipleRegistersRequest(hr, regArray);

                trans = new ModbusTCPTransaction(con);
                trans.setRequest(wrtReq);

                Log.i("TCP Client", "WMR transaction ready ...");

                try {
                    trans.execute();
                    wrtResp = (WriteMultipleRegistersResponse) trans.getResponse();
                } catch (Exception e) {
                    Log.e (TAG, "Execute error", e);
                }

                Log.i("TCP Client", "transaction executed");
                break;

            default:
                Log.e(TAG, "Invalid request");
        }
    }

    public void setState(int requestedState) {
         Log.i("LightControllerComm", "Turning lights ON");
         return;
     }

     public int getState() {
         return state;
     }
}
