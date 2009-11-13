package edu.stanford.movisign;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


public class MoViSign extends Activity {
    /** Called when the activity is first created. */
	private SensorManager sensorMgr;
	private TextView accuracyLabel;
	private TextView xLabel, yLabel, zLabel, directionLabel, inclinationLabel, aboveBelowLabel;
	private boolean accelSupported;
    private Button startLogButton;
    private Button stopLogButton;
	
    private List<Sensor> sensors;
    private Sensor accSensor;
    private float oldX, oldY, oldZ = 0f; 
    private boolean logging = false;
    
  	public static volatile float kFilteringFactor = (float)0.05;
	public static volatile float azimuth = (float) 0;
	public static volatile float pitch = (float) 0;
	public static volatile float roll = (float)0;

	public static volatile float accelX = (float) 0;
	public static volatile float accelY = (float) 0;
	public static volatile float accelZ = (float)0;

	private File root = null;
	private File OrientationLog = null;
	private File AccelerometerLog = null;
	public BufferedWriter OrientationWriter = null ;
	public BufferedWriter AccelerometerWriter = null;
    private String TAG = "movisign";
	
	public File addFile(String filename) throws Exception{
        File directory = new File(Environment.getExternalStorageDirectory().getPath()+"/movisign");

        if (!directory.exists()) {
                directory.mkdir();
        }

        File logfile = new File(directory.getPath()+"/"+filename);
        if (!logfile.exists() && directory.exists()){
                try {
                    logfile.createNewFile();
                } catch (IOException e) {
                        Log.d(TAG,"File creation failed for " + logfile);
                }
        }
        return logfile;
    }

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = Environment.getExternalStorageDirectory();
        try {
            OrientationLog = addFile("orientation.log");
			AccelerometerLog = addFile("accelerometer.log");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (OrientationLog.exists() && OrientationLog.canWrite()){
        	try {
				OrientationWriter = new BufferedWriter(new FileWriter(OrientationLog));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
	    
        if (AccelerometerLog.exists() && AccelerometerLog.canWrite()){
        	try {
				AccelerometerWriter = new BufferedWriter(new FileWriter(AccelerometerLog));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        setContentView(R.layout.main);
        accuracyLabel = (TextView) findViewById(R.id.accuracyLabel);
        xLabel = (TextView) findViewById(R.id.xLabel);
        yLabel = (TextView) findViewById(R.id.yLabel);
        zLabel = (TextView) findViewById(R.id.zLabel);
        startLogButton = (Button) findViewById(R.id.ButtonStartLog);
        /*startLogButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        	    logging = true;
        	  }
        });*/
        stopLogButton = (Button) findViewById(R.id.ButtonStopLog);
        /*stopLogButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        	    logging = false;
        	  }
        });*/
        directionLabel = (TextView) findViewById(R.id.directionLabel);
        inclinationLabel = (TextView) findViewById(R.id.inclinationLabel);
        aboveBelowLabel = (TextView) findViewById(R.id.aboveBelowLabel);
        sensorMgr = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() > 0)
        {
        	accSensor = sensors.get(0);
        	accelSupported = true;
        } else{
        	accelSupported = false;
        }
       
    	sensorMgr.registerListener(mySensorListener,
 			   sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
 			   SensorManager.SENSOR_DELAY_FASTEST);
    	sensorMgr.registerListener(mySensorListener, 
    		   sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
 			   SensorManager.SENSOR_DELAY_FASTEST);




    }
	
    /*private void updateScreen(float x, float y, float z, float direction, float inclination, float aboveOrBelow)
    {
	     float thisX = x - oldX * 10;
	     float thisY = y - oldY * 10;
	     float thisZ = z - oldZ * 10;
 
    	
		 xLabel.setText(String.format("X: %+2.5f (%+2.5f)", (x), oldX));
		 yLabel.setText(String.format("Y: %+2.5f (%+2.5f)", (y), oldY));
		 zLabel.setText(String.format("Z: %+2.5f (%+2.5f)", (z), oldZ));
		 
		 directionLabel.setText(String.format("Direction: %+2.5f", direction));
		 inclinationLabel.setText(String.format("Inclination: %+2.5f", inclination));
		 if(aboveOrBelow > 0 ){
			 aboveBelowLabel.setText("Up");
		 }else{
			 aboveBelowLabel.setText("Down");
		 }
	
    	Log.v("movisign" , "vals " + x + ", " + y + ", "+z);
	    oldX = x;
	    oldY = y;
	    oldZ = z;
    }*/
    private final SensorEventListener mySensorListener = new SensorEventListener()
    {
    	 public void onSensorChanged(SensorEvent event)
	     {
    		  float vals[] = event.values;
	          if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
	          {
	             azimuth =(float) ((vals[0] * kFilteringFactor) + 
	                (azimuth * (1.0 - kFilteringFactor)));
	             pitch = (float) ((vals[1] * kFilteringFactor) + 
	                (pitch * (1.0 - kFilteringFactor)));
	             roll = (float) ((vals[2] * kFilteringFactor) + 
	 	                (roll * (1.0 - kFilteringFactor)));
	             
	             
	            try {
	            	OrientationWriter.write(azimuth + " " + pitch + " " + roll + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             
	          }
    		 if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
             {
    			 accelX =(float) ((vals[0] * kFilteringFactor) + 
    		                (accelX * (1.0 - kFilteringFactor)));
    			 accelY = (float) ((vals[1] * kFilteringFactor) + 
    		                (accelY * (1.0 - kFilteringFactor)));
    			 accelZ = (float) ((vals[2] * kFilteringFactor) + 
    		 	                (accelZ * (1.0 - kFilteringFactor)));
    			 
    			 try {
    				 
    				 AccelerometerWriter.write(accelX + " " + accelY + " " + accelZ + "\n");
 				} catch (IOException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}
             }
	     }
	     
	     public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    	 /*switch (accuracy) {
				case SENSOR_STATUS_UNRELIABLE:
					accuracyLabel.setText(R.string.accuracyUnreliable);
					break;
				case SENSOR_STATUS_ACCURACY_LOW:
					accuracyLabel.setText(R.string.accuracyLow);
					break;
				case SENSOR_STATUS_ACCURACY_MEDIUM:
					accuracyLabel.setText(R.string.accuracyMedium);
					break;
				case SENSOR_STATUS_ACCURACY_HIGH:
					accuracyLabel.setText(R.string.accuracyHigh);
					break;
			 }*/
	     }


		
    };
   
    @Override
    protected void onResume()
    {
    	super.onResume();
    	Log.d(TAG,"resuming");
    	//sensorMgr.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);   
		if (!accelSupported) {
			// on accelerometer on this device
			accuracyLabel.setText(R.string.noAccelerometer);
		}
    }
    
   
    @Override
    protected void onStop()
    {     
    	try {
			OrientationWriter.close();
			AccelerometerWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	sensorMgr.unregisterListener(mySensorListener);
     super.onStop();
    } 
}