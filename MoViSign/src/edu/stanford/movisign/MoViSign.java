package edu.stanford.movisign;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MoViSign extends Activity {
    /** Called when the activity is first created. */
	private SensorManager sensorMgr;
	private TextView accuracyLabel;
	private TextView xLabel, yLabel, zLabel, directionLabel, inclinationLabel, aboveBelowLabel, LogLabel;
	private boolean accelSupported;
   /* private Button startLogButton;
    private Button stopLogButton;*/
	private Button NameOKButton = null;
	private Button SignSuccButton = null;
	private Button SignFailButton =null;
    private ToggleButton LogButton = null;
    
	
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
	private File OrientationTempLog = null;
	private File AccelerometerTempLog = null;
	private File MergedTempLog = null;
	public BufferedWriter OrientationTempWriter = null ;
	public BufferedWriter AccelerometerTempWriter = null;
	public BufferedWriter MergedTempWriter = null;
    private String TAG = "movisign";
    private String SubjectName = null;
    private Dialog inputName = null;
    private Dialog checkDialog = null;
    private EditText NameInputBox = null;
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
        logging = false;
        setContentView(R.layout.main);
        accuracyLabel = (TextView) findViewById(R.id.accuracyLabel);
        xLabel = (TextView) findViewById(R.id.xLabel);
        yLabel = (TextView) findViewById(R.id.yLabel);
        zLabel = (TextView) findViewById(R.id.zLabel);
        LogLabel = (TextView) findViewById(R.id.LogLabel);
        /*startLogButton = (Button) findViewById(R.id.ButtonStartLog);
        startLogButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v) {
        		startLogging();
        	}
        });
        stopLogButton = (Button) findViewById(R.id.ButtonStopLog);
        stopLogButton.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		stopLogging();
            	saveFile(true, "Gene");
        	}
        });*/
        if (SubjectName == null){
	        inputName = new Dialog(this); 
	        inputName.setTitle("Tester's Name");
	        inputName.setContentView(R.layout.dialog);
	        inputName.show();
	
	        NameInputBox = (EditText) inputName.findViewById(R.id.TesterName); 
	        NameInputBox.setSingleLine(true);
	        NameOKButton = (Button) inputName.findViewById(R.id.ButtonNameOK);
	        NameOKButton.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SubjectName = NameInputBox.getText().toString();
					inputName.dismiss();
				}
	        });
        }
        
        checkDialog = new Dialog(this); 
    	checkDialog.setTitle("Is this a successful signature?");
    	checkDialog.setContentView(R.layout.successfail);
    	SignSuccButton = (Button) checkDialog.findViewById(R.id.ButtonSucess);
    	 SignSuccButton.setOnClickListener(new OnClickListener(){
       	public void onClick(View v){
       		saveFile(true, SubjectName);
       		checkDialog.dismiss();
       	}
        });
    	SignFailButton = (Button) checkDialog.findViewById(R.id.ButtonFail);
    	SignFailButton.setOnClickListener(new OnClickListener(){
       	public void onClick(View v){
       		saveFile(false, SubjectName);
       		checkDialog.dismiss();
       	}
        });
        
        LogButton = (ToggleButton) findViewById(R.id.LogButton);
        LogButton.setOnTouchListener(new OnTouchListener() {
            
            public boolean onTouch(View v, MotionEvent event) {
                 // TODO Auto-generated method stub
                 
                 if(event.getAction()==MotionEvent.ACTION_DOWN){
                	 startLogging();
                	 LogLabel.setText("Logging.....");
                	 LogButton.setChecked(true);
                	 LogButton.setText("Release to Stop Logging");
                 }else if (event.getAction()==MotionEvent.ACTION_UP) {
                	 LogLabel.setText("Stop Logging.....");
                	 stopLogging();
                	 saveFile(true, SubjectName);
                     
                 	 
                     checkDialog.show();
                 	
                    LogLabel.setText("Stopped");
                 	LogButton.setChecked(false);
                 	LogButton.setText("Click to Start Logging");
                 }
                 
                 return true;
            }
       }); 
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
        
    }
	public void saveFile(boolean correctness, String name){
		if(name == null){
			name = new String("Default");
		}
		if( (OrientationTempLog != null) && (AccelerometerTempLog != null)){
			Date date = new Date();
			try {
				OrientationTempLog.renameTo(addFile("Orientation_"+ name + "_" + correctness + "_" + date.getTime()+".log") );
				AccelerometerTempLog.renameTo(addFile("accelerometer_"+ name + "_" + correctness + "_" + date.getTime()+".log") );
				MergedTempLog.renameTo(addFile("merged_"+ name + "_" + correctness + "_" + date.getTime()+".log") );			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "save log file");
		}
	}
	public void startLogging(){
		if( logging == false){
			try {
				OrientationTempLog = addFile("Orientation.temp");
				AccelerometerTempLog =  addFile("Accelerometer.temp");
				MergedTempLog = addFile("Merged.temp");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        if (OrientationTempLog.exists() && OrientationTempLog.canWrite()){
	        	try {
					OrientationTempWriter = new BufferedWriter(new FileWriter(OrientationTempLog));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
		    
	        if (AccelerometerTempLog.exists() && AccelerometerTempLog.canWrite()){
	        	try {
					AccelerometerTempWriter = new BufferedWriter(new FileWriter(AccelerometerTempLog));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
	        if (MergedTempLog.exists() && MergedTempLog.canWrite()){
	        	try {
					MergedTempWriter = new BufferedWriter(new FileWriter(MergedTempLog));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
	        logging = true;
	        Log.d(TAG, "start logging");
		}
	}
	
	public void stopLogging(){
		if (logging == true){
			logging = false;
			
			try {
				OrientationTempWriter.close();
				AccelerometerTempWriter.close();
				MergedTempWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "stop logging");
		}
		
	}
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
	            	if(logging == true){
	            	   Date temp = new Date();
	                   OrientationTempWriter.write(azimuth + " " + pitch + " " + roll + " " + temp.getTime() + "\n");
	                   MergedTempWriter.write(azimuth + " " + pitch + " " + roll + " " + accelX + " " + accelY + " " + accelZ + " " + temp.getTime() + "\n");

	            	}
	            } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch(NullPointerException e){
					Log.e(TAG, e.getCause().getMessage());
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
    				 if( logging == true ){
        				 Date temp = new Date();
    					 AccelerometerTempWriter.write(accelX + " " + accelY + " " + accelZ + " " + temp.getTime() + "\n");
    					 MergedTempWriter.write(azimuth + " " + pitch + " " + roll + " " + accelX + " " + accelY + " " + accelZ + " " + temp.getTime() + "\n");
    				 }
    			} catch (IOException e) {
 					// TODO Auto-generated catch block
 					e.printStackTrace();
 				}catch(NullPointerException e){
 					Log.e(TAG, e.getCause().getMessage());
					e.printStackTrace();
				}
             }
	     }
	     
	     public void onAccuracyChanged(Sensor sensor, int accuracy) {}


		
    };
   
    
    @Override
    protected void onResume()
    {
    	super.onResume();	
       	sensorMgr.registerListener(mySensorListener,
  			   sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
  			   SensorManager.SENSOR_DELAY_FASTEST);
     	sensorMgr.registerListener(mySensorListener, 
     		   sensorMgr.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
  			   SensorManager.SENSOR_DELAY_FASTEST);

    	
		if (!accelSupported) {
			accuracyLabel.setText(R.string.noAccelerometer);
		}
    }
    
    protected void onPause(){
    	super.onPause();
    }
    
    @Override
    protected void onStop()
    {         	
    	sensorMgr.unregisterListener(mySensorListener);
    	super.onStop();

    } 
}