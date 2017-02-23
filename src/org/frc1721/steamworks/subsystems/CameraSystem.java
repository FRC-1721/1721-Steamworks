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
	public static VisionThread visionThread;
	private final Object imgLock = new Object();
	private static double visionArea;
	private static double visionCenter;
	private static final double gearAR = 2.2, gearARTol = 0.2;
	private static boolean newData = false;
	private static double targetDistance = 0.0;
	private static UsbCamera gearCamera, ballCamera;
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ProcessCameraData ());
	}

	public CameraSystem () {
		UsbCamera ballCamera = CameraServer.getInstance().startAutomaticCapture(0);
		UsbCamera gearCamera = CameraServer.getInstance().startAutomaticCapture(1);
		ballCamera.setResolution(320,240);
		ballCamera.setFPS(15);
		gearCamera.setResolution(640,480);
		gearCamera.setFPS(10);
		visionThread = new VisionThread(gearCamera, new GripPipeline(), pipeline -> {
				int n = pipeline.filterContoursOutput().size()
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
		        				newData = False;
		        			}
		        		}
		            }
		        }
		    });
		visionThread.start();
	}
	
	public void stop() {
		visionThread.suspend();
	}
	
	public void start() {
		visionThread.resume();
	}
	
	public void processData () {
		if (newData) {
			// ToDo add stuff here
		}
	}
	
	public double getTargetDistance () {
		return targetDistance;
	}
	
	public void updateSmartDashboard() {
		/** Vision Stuff **/
		SmartDashboard.putNumber("Vision Center", visionCenter);
		SmartDashboard.putNumber("Vision Area", visionArea);
		//SmartDashboard.putNumber("Vision distance", targetDistance);
	}
}
