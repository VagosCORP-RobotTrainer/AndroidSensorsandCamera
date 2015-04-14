package com.vagoscorp.vc_rtcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import vclibs.communication.android.Comunic;
import vclibs.communication.Eventos.OnConnectionListener;
import vclibs.communication.Eventos.OnComunicationListener;

public class MainActivity extends Activity implements SensorEventListener {

    static Camera mCamera;
    TextView textview1;
    TextView textview2;
    TextView textview3;
    SurfaceView sv;
    static SurfaceHolder sHolder;
    LocationListener locationListener;
    BluetoothDevice[] BondedDevices;
    BluetoothAdapter BTAdapter;
    BluetoothDevice mDevice;
    int mDeviceIndex;
    int distancia = 0;
    int angulo = 0;

    public static final String SI = "SIP";
    public static final String SP = "SPort";

    public static final String LD = "LD";
    private final int REQUEST_ENABLE_BT = 3;
    private final int SEL_BT_DEVICE = 2;
    public static final String indev = "indev";
    private final int defIndex = 0;
    public int index;
    public String[] DdeviceNames;
    Comunic comunic;
    Comunic comunicPIC;
    int lmag = 0;

    String TAG = "VC Camera";

    SensorManager sensorManager;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;

    float ax = 0;
    float ay = 0;
    float az = 0;
    float ox = 0;
    float oy = 0;
    float oz = 0;
    static boolean send = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_main);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        comunic = new Comunic();
        comunicPIC = new Comunic();
        textview1 = (TextView) findViewById(R.id.textView1);
        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);

        sv = (SurfaceView) findViewById(R.id.surfaceView1);
        sHolder = sv.getHolder();
        sHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    Log.d(TAG,
                            "Error setting camera preview: " + e.getMessage());
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // empty. Take care of releasing the Camera preview in your
                // activity.
                try {
//					mCamera.stopPreview();
                    mCamera.release();
                } catch (Exception e) {
                    // ignore: tried to stop a non-existent preview
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // If your preview can change or rotate, take care of those events here.
                // Make sure to stop the preview before resizing or reformatting it.

                if (holder.getSurface() == null){
                    // preview surface does not exist
                    return;
                }

                // stop preview before making changes
                try {
                    mCamera.stopPreview();
                } catch (Exception e){
                    // ignore: tried to stop a non-existent preview
                }

                // set preview size and make any resize, rotate or
                // reformatting changes here

                // start preview with new settings
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();

                } catch (Exception e){
                    Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                }
            }
        });

        // Create our Preview view and set it as the content of our activity.
        // mPreview = new Preview(this, mCamera);
        // FrameLayout preview = (FrameLayout)
        // findViewById(R.id.camera_preview);
        // preview.addView(mPreview);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location
                // provider.
                // makeUseOfNewLocation(location);
                textview1.setText(location.toString());
            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location
        // updates
        // locationManager.requestLocationUpdates(
        // LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);
    }

//    public void setMov() {
//        Intent CS = new Intent(this, Destino.class);
//        CS.putExtra(SI, distancia);
//        CS.putExtra(SP, angulo);
//        startActivityForResult(CS, 1);
//    }

    private void initBTD(BluetoothDevice[] BonDev) {
//        if (BonDev.length > 0) {
//            // RX.append("no gut");
//            if (BonDev.length < index)
//                index = 0;
//            mDevice = BondedDevices[index];
//            // SD.setText(mDevice.getName() + "\n" + mDevice.getAddress());
//            // Conect.setEnabled(true);
//        } else {
//            // RX.append("gut");
//            // SD.setText(R.string.Ser_Dat);
//            // Conect.setEnabled(false);
//        }
    }

    @Override
    protected void onStart() {
//        SharedPreferences shapre = getPreferences(MODE_PRIVATE);
//        index = shapre.getInt(indev, defIndex);
//        // If Bluetooth is not on, request that it be enabled.
//        if (BTAdapter.isEnabled()) {
//            BondedDevices = BTAdapter.getBondedDevices().toArray(
//                    new BluetoothDevice[0]);
//            initBTD(BondedDevices);
//        } else {
//            Intent enableIntent = new Intent(
//                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//        }
        connect();
        connectPIC();
        super.onStart();
    }

    public void connect() {
        comunic = new Comunic(this, 2550);
        comunic.setConnectionListener(new OnConnectionListener() {

            @Override
            public void onConnectionstablished() {
//                comunic.enviar("B");
//                comunic.enviar(distancia);
//                comunic.enviar(angulo);
            }

            @Override
            public void onConnectionfinished() {
                connect();
            }
        });
        comunic.setComunicationListener(new OnComunicationListener() {

            @Override
            public void onDataReceived(int nbytes, String dato, int[] ndat, byte[] bdat) {
                for(int val:ndat) {
                    if(val != 'L')
                        comunicPIC.enviar(val);
                    else
                        takePic();
                }
            }
        });
        comunic.execute();
    }

    public void connectPIC() {
        comunicPIC = new Comunic(this, "10.0.2.12", 2000);
        comunicPIC.setConnectionListener(new OnConnectionListener() {

            @Override
            public void onConnectionstablished() {
//                comunicPIC.enviar("B");
//                comunicPIC.enviar(distancia);
//                comunicPIC.enviar(angulo);
            }

            @Override
            public void onConnectionfinished() {

            }
        });
        comunicPIC.setComunicationListener(new OnComunicationListener() {

            @Override
            public void onDataReceived(int nbytes, String dato, int[] ndat, byte[] bdat) {
                for(int val:ndat) {
                    if(val == 'L')
                        takePic();
                }
            }
        });
        comunicPIC.execute();
    }

    public void disconnect() {
        comunic.Detener_Actividad();
    }

    public void Chan_Ser() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1: {
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        // mCamera.stopPreview();
        mCamera.release();
        comunic.Detener_Actividad();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
//        mCamera.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mCamera = getCameraInstance();
        if (mCamera != null)
            mCamera.startPreview();
    }

    /** Check if this device has a camera */
    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    /** Handles data for jpeg picture */
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            try {
                // write to local sandbox file system
                // outStream =
                // CameraDemo.this.openFileOutput(String.format("%d.jpg",
                // System.currentTimeMillis()), 0);
                // Or write to sdcard
                String loc = textview1.getText().toString();
                outStream = new FileOutputStream(
                        String.format(
                                Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        + "/%d.jpg", System.currentTimeMillis()));
                outStream.write(data);
                outStream.close();
                outStream = new FileOutputStream(
                        String.format(
                                Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        + "/%d.txt", System.currentTimeMillis()));
                outStream.write(loc.getBytes());
                outStream.close();
                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    // Add a listener to the Capture button

    private void takePic() {
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public void tomarfoto(View view) {
        // mCamera.takePicture(null, null, mPicture);
        takePic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
//		} else if (id == R.id.oMaps) {
//			GMaps();
//			return true;
//        } else if (id == R.id.conn) {
//            connect();
//            return true;
//        } else if (id == R.id.chser) {
//            Chan_Ser();
//            return true;
//        } else if (id == R.id.disconn) {
//            disconnect();
//            return true;
//        } else if (id == R.id.smov) {
//            setMov();
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//	public void GMaps() {
//		Toast.makeText(this, "En una Versión posterior", Toast.LENGTH_SHORT).show();
//	}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            // textview.setText("X = "+ax+" Y = "+ay+" Z = "+az);
            // textView2.setText("X = "+ay);
            // textView3.setText("X = "+az);
            textview2.setText("X = " + ax + " Y = " + ay + " Z = " + az);
        }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // lmag = event.values.length;
            oz = event.values[0];
            ox = event.values[1];
            oy = event.values[2];
            textview3.setText("X = " + ox + " Y = " + oy + " Z = " + oz);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}