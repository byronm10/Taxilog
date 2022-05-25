package com.example.nuevotaxilog;

import androidx.annotation.NonNull;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    EditText etnm;
    Button bt1;
    private LocationManager locationManager;
    private TextView textviewlo, textviewla;
    private Socket usuario;
    private PrintWriter mostrar;
    private Handler handler;
    private EditText Direccion, puerto, sms;
    private Button Enviar, button, btenviar;
    private String mensaje;
    private String mensaje2;
    private com.example.nuevotaxilog.UDPSender sender;
    private String Ipinstanciabyron= "52.72.138.223";
    int puertoinstanciabyron= 40001;
    private String Ipinstancialuis= "34.232.41.21";
    int puertoinstancialuis= 40001;
    private BluetoothSocket socket = null;
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public String btAddress = "";
    int puert = 0;
    String rpm= "";
    String vehiculo= "";
    int i = 0;
    Timestamp tiempo = new Timestamp(System.currentTimeMillis());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.activity_main);
        Direccion = (EditText) findViewById(R.id.etdireccionip);
        puerto = (EditText) findViewById(R.id.etpuerto);
        button = (Button) findViewById(R.id.button);
        textviewlo = (TextView) findViewById(R.id.textView2);
        textviewla = (TextView) findViewById(R.id.textView3);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String[] valores = {"Vehiculo 1", "Vehiculo 2"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valores));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1003);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1004);
        }

        connectBlueTooth();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {   String seleccion = spinner.getSelectedItem().toString();
                    if(seleccion.equals("Vehiculo 1")){
                        vehiculo= "1";
                        Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    } else if (seleccion.equals("Vehiculo 2")){
                        vehiculo= "2";
                        Toast.makeText(adapterView.getContext(), (String) adapterView.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(MainActivity.this, "Seleccione un Vehiculo", Toast.LENGTH_LONG).show();
            }
        });


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Date d = new Date((location.getTime()));
                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textviewlo.setText(String.valueOf(location.getLongitude()));
                                textviewla.setText(String.valueOf(location.getLatitude()));
                                long Timestamp = location.getTime();

                                RPMCommand engineRpmCommand = new RPMCommand();
                                String rpm = "";
                                try {
                                    engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                                    rpm = engineRpmCommand.getFormattedResult().replace("RPM", "");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                Date d = new Date((location.getTime()));
                                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                mensaje = location.getLatitude() +"%"+ location.getLongitude()+"%"+(f.format(d))+"%"+vehiculo+"%"+rpm;

                            }
                        });




                    }
                }).start();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String currentDateandTime = simpleDateFormat.format(new Date());
            }
        }); //
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable = new Runnable() {
                    public void run() {

                        ArrayList <String> ips =new ArrayList<>();
                        ips.add(String.valueOf(Ipinstanciabyron));
                        ips.add(String.valueOf(Ipinstancialuis));
                        String port = String.valueOf(puertoinstanciabyron);

                        String portluis = String.valueOf(puertoinstancialuis);

                        if (!ips.isEmpty() && puertoinstanciabyron!=0 ) {
                            try {
                                sender = new UDPSender(Integer.parseInt(port), ips);
                                sender.send(mensaje);
                                Toast.makeText(MainActivity.this, "Mensaje Enviado a instancia Byron", Toast.LENGTH_LONG).show();
                                Toast.makeText(MainActivity.this, "Mensaje Enviado a instancia Luis", Toast.LENGTH_LONG).show();
                                Toast.makeText(MainActivity.this, "Mensaje"+ mensaje, Toast.LENGTH_LONG).show();
                                handler.postDelayed(this, 10000); // 5 seconds.


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, " No es necesario Ingresar valores", Toast.LENGTH_LONG).show();

                        }

                    }
                };
                runnable.run();
            }
        });

        class TimeStampExample {

            // 2021.03.24.16.34.26
            private final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        }
        Date date = new Date();
    }

    public void connectBlueTooth() {

        BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
        Set <BluetoothDevice> devices = bt.getBondedDevices();

        for (BluetoothDevice device : devices) {
            Toast.makeText(getApplicationContext(), device.getName(), Toast.LENGTH_SHORT).show();
            if (device.getName().trim().equals("OBDII")) {
                btAddress = device.getAddress();
                Toast.makeText(getApplicationContext(), "OBDII Conectado Exitosamente", Toast.LENGTH_SHORT).show();
            }
        }

        if (btAddress.equals("")) {
            Toast.makeText(getApplicationContext(), "No hay un sensor con OBDII emparejado", Toast.LENGTH_SHORT).show();
        }

        if (!btAddress.equals("")) {
            try {
                // Iniciamos socket de Bluetooth.
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = btAdapter.getRemoteDevice(btAddress);

                socket = device.createRfcommSocketToServiceRecord(uuid);
                socket.connect();
                InputStream io = socket.getInputStream();
                OutputStream ou = socket.getOutputStream();
                Toast.makeText(getApplicationContext(), "Dispositivo OBDII emparejado.", Toast.LENGTH_SHORT).show();
                new EchoOffCommand().run(io, ou);
                new LineFeedOffCommand().run(io, ou);
                new TimeoutCommand(125).run(io, ou);
                new SelectProtocolCommand(ObdProtocols.AUTO).run(io, ou);
            }  catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

    }





}