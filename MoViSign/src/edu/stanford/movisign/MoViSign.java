package edu.stanford.movisign;

import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
import static android.hardware.SensorManager.SENSOR_STATUS_UNRELIABLE;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class MoViSign extends Activity {
    /** Called when the activity is first created. */
	private SensorManager sensorMgr;
	private TextView accuracyLabel;
	private TextView xLabel, yLabel, zLabel;
	private boolean accelSupported;

    private List<Sensor> sensors;
    private Sensor accSensor;
    private float oldX, oldY, oldZ = 0f; 
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        accuracyLabel = (TextView) findViewById(R.id.accuracyLabel);
        xLabel = (TextView) findViewById(R.id.xLabel);
        yLabel = (TextView) findViewById(R.id.yLabel);
        zLabel = (TextView) findViewById(R.id.zLabel);
        sensorMgr = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() > 0)
        {
        	accSensor = sensors.get(0);
        	accelSupported = true;
        } else{
        	accelSupported = false;
        }
    }
	
    private void updateScreen(float x, float y, float z)
    {
	     /*float thisX = x - oldX * 10;
	     float thisY = y - oldY * 10;
	     float thisZ = z - oldZ * 10;*/
	     
		 xLabel.setText(String.format("X: %+2.5f (%+2.5f)", (x), oldX));
		 yLabel.setText(String.format("Y: %+2.5f (%+2.5f)", (y), oldY));
		 zLabel.setText(String.format("Z: %+2.5f (%+2.5f)", (z), oldZ));

	     oldX = x;
	     oldY = y;
	     oldZ = z;
    }
   
    private final SensorEventListener mySensorListener = new SensorEventListener()
    {
	     public void onSensorChanged(SensorEvent event)
	     {
	          updateScreen(event.values[0],
	                    event.values[1],
	                    event.values[2]);
	     }
	     
	     public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    	 switch (accuracy) {
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
			 }
	     }
    };
   
    @Override
    protected void onResume()
    {
    	super.onResume();
    	sensorMgr.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);   
		if (!accelSupported) {
			// on accelerometer on this device
			accuracyLabel.setText(R.string.noAccelerometer);
		}

    }
    
   
    @Override
    protected void onStop()
    {     
    	sensorMgr.unregisterListener(mySensorListener);
     super.onStop();
    } 
}