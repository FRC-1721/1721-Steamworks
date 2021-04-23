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
	private static double visionCenterX1;
	private static double visionArea1;
	private static double visionCenterX2;
	private static double visionArea2;
	private static double visionW1, visionW2, visionH1, visionH2;
	private static boolean newData = false;
	private static double targetDistance = 0.0;
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ProcessCameraData ());
	}

	public CameraSystem () {
		UsbCamera cam0 = CameraServer.getInstance().startAutomaticCapture(0);
		UsbCamera cam1 = CameraServer.getInstance().startAutomaticCapture(1);
		cam0.setResolution(320,240);
		cam0.setFPS(15);
		cam1.setResolution(320,240);
		cam1.setFPS(10);
		visionThread = new VisionThread(cam1, new GripPipeline(), pipeline -> {
		        if (!pipeline.filterContoursOutput().isEmpty()) {
		            Rect r1 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
		            synchronized (imgLock) {
		            	visionW1 = r1.width;
		            	visionH1 = r1.height;
		                visionCenterX1 = r1.x + (r1.width / 2);
		                visionArea1 = r1.width*r1.height;
		            }
		            if (pipeline.filterContoursOutput().size() > 1) {
		            	Rect r2 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(1));
		            	synchronized (imgLock) {
		            		visionW2 = r2.width;
			            	visionH2 = r2.height;
			                visionCenterX2 = r2.x + (r2.width / 2);
			                visionArea2 = r2.width*r2.height;
			            }
		            }
		            newData  = true;
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
			double area = visionArea1;
			if (visionArea2 > visionArea1) area = visionArea2;
			targetDistance = 120.0/Math.sqrt(area);
		}
	}
	
	public double getTargetDistance () {
		return targetDistance;
	}
	
	public void updateSmartDashboard() {
		/** Vision Stuff **/
		SmartDashboard.putNumber("Vision Center1", visionCenterX1);
		SmartDashboard.putNumber("Vision Center2", visionCenterX2);
		SmartDashboard.putNumber("Vision W1", visionW1);
		SmartDashboard.putNumber("Vision H1", visionH1);
		SmartDashboard.putNumber("Vision W2", visionW2);
		SmartDashboard.putNumber("Vision H2", visionH2);
		//SmartDashboard.putNumber("Vision distance", targetDistance);
	}
}
