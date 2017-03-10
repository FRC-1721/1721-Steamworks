package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.GripPipeline;
import org.frc1721.steamworks.commands.ProcessCameraData;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

@SuppressWarnings("unused")
public class CameraSystem extends Subsystem {
	public  VisionThread visionThread;
	private final Object imgLock = new Object();
	private  double visionArea;
	private  double visionCenter;
	private  final double gearAR = 2.2, gearARTol = 0.2;
	public  boolean newData = false;
	private  double camWidth = 640.0;
	public  double rawDist, rawAngle, realDist, realAngle;
	private  double targetDistance = 0.0;
	private  UsbCamera gearCamera, ballCamera;
	public  double distM, distC;
	public  double angleM, angleC;
	
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ProcessCameraData ());
	}

	public CameraSystem () {
		// ToDo Redo numbering
		//UsbCamera ballCamera = CameraServer.getInstance().startAutomaticCapture(0);
		UsbCamera gearCamera = CameraServer.getInstance().startAutomaticCapture(0);
		/* ballCamera.setResolution(320,240);
		ballCamera.setFPS(15); */
		gearCamera.setResolution(640,480);
		gearCamera.setFPS(10);
		visionThread = new VisionThread(gearCamera, new GripPipeline(), pipeline -> {
				int n = pipeline.filterContoursOutput().size();
		        if (n > 1) {
		        	synchronized (imgLock) {
		        		// Assume the first two sets of countours are the correct ones
		        		visionArea = 0.0;
		        		visionCenter = 0.0;
		        		// assume the data is good to begin with
		        		newData = true;
		        		for (int i=0; i<2; i++) {
		        			Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(i));
		        			// Check the aspect ratio is within tolerance
		        			if (Math.abs(r.height/r.width - gearAR) < gearARTol) {
		        				visionArea += r.width*r.height;
		        				visionCenter += r.x;
		        			} else {
		        				newData = false;
		        			}
		        		}
		            }
		        }
		    });
		visionThread.start();
	}
	
	public void stop() {
		//visionThread.suspend();
	}
	
	public void start() {
		//visionThread.resume();
		newData = false;
	}
	
	public boolean processData () {
		if (newData) {
			synchronized(this){
				rawDist = 1.0/Math.sqrt(visionArea);
				rawAngle = (visionCenter - camWidth)/rawDist;
				newData = false;
				realDist = distM*rawDist + distC;
				realAngle = angleM*rawAngle + angleC;
			}
			return true;
		} else {
			return false;
		}
	}
	

	
	public double getTargetDistance () {
		return targetDistance;
	}
	
	public void updateSmartDashboard() {
		/** Vision Stuff **/
		SmartDashboard.putNumber("Vision distM", distM);
		SmartDashboard.putNumber("Vision distC", distC);
		SmartDashboard.putNumber("Vision angleM", angleM);
		SmartDashboard.putNumber("Vision angleC", angleC);
		SmartDashboard.putNumber("Vision realDist", realDist);
		SmartDashboard.putNumber("Vision realAngle", realAngle);
		SmartDashboard.putNumber("Vision rawDist", rawDist);
		SmartDashboard.putNumber("Vision rawAngle", rawAngle);
		//SmartDashboard.putNumber("Vision Area", visionArea);
		//SmartDashboard.putNumber("Vision distance", targetDistance);
	}
}
