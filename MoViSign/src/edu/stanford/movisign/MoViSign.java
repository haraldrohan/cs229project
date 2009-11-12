package edu.stanford.movisign;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
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
    private boolean logging;
    
  	public static volatile float kFilteringFactor = (float)0.05;
	public static volatile float direction = (float) 0;
	public static volatile float inclination = 0;
	public static volatile float rollingZ = (float)0;
	public static float aboveOrBelow = (float)0;

    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("movisign", "onCreate");
        Log.e("error", "onCreate");

        setContentView(R.layout.main);
        accuracyLabel = (TextView) findViewById(R.id.accuracyLabel);
        xLabel = (TextView) findViewById(R.id.xLabel);
        yLabel = (TextView) findViewById(R.id.yLabel);
        zLabel = (TextView) findViewById(R.id.zLabel);
        /*startLogButton = (Button) findViewById(R.id.ButtonStartLog);
        startLogButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        	    //finish();
        	  }
        });
        stopLogButton = (Button) findViewById(R.id.ButtonStopLog);*/
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
 			   SensorManager.SENSOR_DELAY_UI);
    	sensorMgr.registerListener(mySensorListener, 
 			sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
 			SensorManager.SENSOR_DELAY_UI);




    }
	
    private void updateScreen(float x, float y, float z, float direction, float inclination, float aboveOrBelow)
    {
	     /*float thisX = x - oldX * 10;
	     float thisY = y - oldY * 10;
	     float thisZ = z - oldZ * 10;*/
 
    	
		 /*xLabel.setText(String.format("X: %+2.5f (%+2.5f)", (x), oldX));
		 yLabel.setText(String.format("Y: %+2.5f (%+2.5f)", (y), oldY));
		 zLabel.setText(String.format("Z: %+2.5f (%+2.5f)", (z), oldZ));
		 
		 directionLabel.setText(String.format("Direction: %+2.5f", direction));
		 inclinationLabel.setText(String.format("Inclination: %+2.5f", inclination));
		 if(aboveOrBelow > 0 ){
			 aboveBelowLabel.setText("Up");
		 }else{
			 aboveBelowLabel.setText("Down");
		 }*/
	
    	Log.v("movisign" , "vals " + x + ", " + y + ", "+z);
	    oldX = x;
	    oldY = y;
	    oldZ = z;
    }
    private final SensorEventListener mySensorListener = new SensorEventListener()
    {

    	 public void onSensorChanged(SensorEvent event)
	     {
   	      Log.e("error", "onSensorChanged");
    		 /*float vals[] = event.values;
	          if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
	          {
	             float rawDirection = vals[0];

	             direction =(float) ((rawDirection * kFilteringFactor) + 
	                (direction * (1.0 - kFilteringFactor)));

	             inclination = 
	                (float) ((vals[2] * kFilteringFactor) + 
	                (inclination * (1.0 - kFilteringFactor)));

	                    
	              if(aboveOrBelow > 0)
	                 inclination = inclination * -1;
	              
	             if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
	             {
	                aboveOrBelow =
	                   (float) ((vals[2] * kFilteringFactor) + 
	                   (aboveOrBelow * (1.0 - kFilteringFactor)));
	  	            updateScreen(event.values[SensorManager.DATA_X],
		                    event.values[SensorManager.DATA_Y],
		                    event.values[SensorManager.DATA_Z], direction, inclination, aboveOrBelow);

	             }
	             
	          }*/
    		 if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
             {
                /*aboveOrBelow =
                   (float) ((vals[2] * kFilteringFactor) + 
                   (aboveOrBelow * (1.0 - kFilteringFactor)));*/
  	            updateScreen(event.values[SensorManager.DATA_X],
	                    event.values[SensorManager.DATA_Y],
	                    event.values[SensorManager.DATA_Z], direction, inclination, aboveOrBelow);
  	            Log.d("movisign" , "vals " + event.values[SensorManager.DATA_X] + ", " 
  	        		  	+ event.values[SensorManager.DATA_Y] + ", "+ event.values[SensorManager.DATA_Z]);
  	            Log.e("errors" , "vals " + event.values[SensorManager.DATA_X] + ", " 
	        		  	+ event.values[SensorManager.DATA_Y] + ", "+ event.values[SensorManager.DATA_Z]);
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
    	Log.d("movisign","resuming");
    	//sensorMgr.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);   
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