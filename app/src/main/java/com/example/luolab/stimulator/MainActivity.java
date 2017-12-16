package com.example.luolab.stimulator;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import java.util.HashMap;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    Physicaloid mPhysicaloid;

    Spinner Baud_Spinner;

    SeekBar Ch1_seekBar;
    SeekBar Ch2_seekBar;
    TextView Ch1_Tx;
    TextView Ch2_Tx;

    Button Open_Btn;
    Button Close_Btn;

    boolean Serial_Flag = false;

    boolean Baud_Flags = false;

    PendingIntent mPermissionIntent;


    UsbDevice device;
    UsbManager manager;

    private static final String ACTION_USB_PERMISSION = "com.mobilemerit.usbhost.USB_PERMISSION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPhysicaloid = new Physicaloid(this);

        Ch1_seekBar = (SeekBar) findViewById(R.id.Ch1_Digital_Value_SeekBar);
        Ch1_Tx = (TextView) findViewById(R.id.Ch1_Digital_Value_tx);

        Ch2_seekBar = (SeekBar) findViewById(R.id.Ch2_Digital_Value_SeekBar);
        Ch2_Tx = (TextView) findViewById(R.id.Ch2_Digital_Value_tx);

        CH1_SeekBar_ChangeListener();
        CH2_SeekBar_ChangeListener();

        Baud_Spinner = (Spinner) findViewById(R.id.Baud_Spinner);


        Open_Btn = (Button) findViewById(R.id.Open_Btn);
        Open_Btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*switch(Baud_Spinner.getSelectedItem().toString()){
                    case "9600 baud" :
                        Baud_Flags = mPhysicaloid.setBaudrate(9600);
                        break;
                    case "19200 baud":
                        Baud_Flags = mPhysicaloid.setBaudrate(19200);
                        break;
                    case "38400 baud":
                        Baud_Flags = mPhysicaloid.setBaudrate(38400);
                        break;
                    case "57600 baud":
                        Baud_Flags = mPhysicaloid.setBaudrate(57600);
                        break;
                    case "115200 baud":
                        Baud_Flags = mPhysicaloid.setBaudrate(115200);
                        break;
                    default:
                        Baud_Flags = mPhysicaloid.setBaudrate(9600);
                }*/
                try {
                    if (mPhysicaloid.open()) {
                        Set_UI(true);
                        Serial_Flag = true;
                    }
                }
                catch(Exception io){
                    Toast.makeText(getApplicationContext(), "Cannot open", Toast.LENGTH_LONG).show();
                }
            }
        });

        Close_Btn = (Button) findViewById(R.id.Close_Btn);

        Close_Btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPhysicaloid.close()) {
                    mPhysicaloid.clearReadListener();
                    Set_UI(false);
                    Serial_Flag = false;
                }
            }
        });

        Set_UI(false);
    }

    private void Set_UI(boolean state)
    {
        if(state){
            Open_Btn.setEnabled(false);
            Close_Btn.setEnabled(true);
        }
        else {
            Open_Btn.setEnabled(true);
            Close_Btn.setEnabled(false);
        }
    }
    private void CH1_SeekBar_ChangeListener()
    {
        Ch1_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                Ch1_Tx.setText(String.valueOf(value));
                byte[] buf = new byte[2];

                buf[0] = (byte) ((byte)(value >> 8) | 0x10);
                buf[1] = (byte)value;

                if(Serial_Flag)
                    mPhysicaloid.write(buf, buf.length);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private void CH2_SeekBar_ChangeListener()
    {
        Ch2_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                Ch2_Tx.setText(String.valueOf(value));
                byte[] buf = new byte[2];

                buf[0] = (byte) ((byte)(value >> 8) | 0x20);
                buf[1] = (byte)value;

                if(Serial_Flag)
                    mPhysicaloid.write(buf, buf.length);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
