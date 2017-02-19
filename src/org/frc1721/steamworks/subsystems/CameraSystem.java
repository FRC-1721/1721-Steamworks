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
	private static boolean newData = false;
	private static double targetDistance = 0.0;
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new ProcessCameraData ());
	}

	public CameraSystem () {
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
		        if (!pipeline.filterContoursOutput().isEmpty()) {
		            Rect r1 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
		            synchronized (imgLock) {
		                visionCenterX1 = r1.x + (r1.width / 2);
		                visionArea1 = r1.width*r1.height;
		            }
		            if (pipeline.filterContoursOutput().size() > 1) {
		            	Rect r2 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(1));
		            	synchronized (imgLock) {
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
	public void updateSmartDashboard() {
		/** Vision Stuff **/
		SmartDashboard.putNumber("Vision X1", visionCenterX1);
		SmartDashboard.putNumber("Vision X2", visionCenterX2);
		SmartDashboard.putNumber("Vision A1", visionArea1);
		SmartDashboard.putNumber("Vision A2", visionArea2);
		SmartDashboard.putNumber("Vision distance", targetDistance);
	}
}
