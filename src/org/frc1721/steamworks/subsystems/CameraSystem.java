package org.frc1721.steamworks.subsystems;

import org.frc1721.steamworks.GripPipeline;
import org.frc1721.steamworks.RobotMap;
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
  public VisionThread visionThread;
  private final Object imgLock = new Object();
  private double visionArea;
  private double visionCenter;
  private final double gearAR = 2.2, gearARTol = 0.3;
  public boolean newData = false;
  private double camWidth = 640.0;
  public double rawDist, rawAngle, realDist, realAngle;
  private double targetDistance = 0.0;
  private int visionSample = 0;
  private UsbCamera gearCamera, ballCamera;
  public double distM = 1.0, distC = 0.0;
  public double angleM = 0.0, angleC = 0.0;
  public boolean calibrated = true;
  
  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new ProcessCameraData());
  }

  public CameraSystem() {
    // ToDo Redo numbering
    // UsbCamera ballCamera = CameraServer.getInstance().startAutomaticCapture(0);
    // ballCamera.setResolution(320,240); ballCamera.setFPS(15);
    if (!RobotMap.visionPi) {
      UsbCamera gearCamera =
          CameraServer.getInstance().startAutomaticCapture(0);
      gearCamera.setResolution(640, 480);
      gearCamera.setFPS(10);
      visionThread =
          new VisionThread(gearCamera, new GripPipeline(), pipeline -> {
            int n = pipeline.filterContoursOutput().size();

            if (n > 1) {

              synchronized (imgLock) {
                double tmpArea = 0.0;
                double tmpCenter = 0.0;
                int nSamples = 0;
                // Assume the first two sets of countours are the correct ones

                // assume the data is good to begin with
                for (int i = 0; i < n; i++) {
                  Rect r = Imgproc
                      .boundingRect(pipeline.filterContoursOutput().get(i));
                  // Check the aspect ratio is within tolerance
                  if (Math.abs(r.height / r.width - gearAR) < gearARTol) {
                    tmpArea += r.width * r.height;
                    tmpCenter += r.x;
                    nSamples += 1;
                  }
                }

                if (nSamples == 2) {
                  newData = true;
                  visionArea = tmpArea;
                  visionCenter = tmpCenter;
                }

              }
            }


          });
      visionThread.start();
    }
  }

  public void stop() {
    // visionThread.suspend();
  }

  public void start() {
    // visionThread.resume();
    newData = false;
  }

  public boolean processData() {
    synchronized (this) {
      if (RobotMap.visionPi) {
        int sdVisionSample =
            (int) SmartDashboard.getNumber("visionSample", 0.0);
        if (sdVisionSample > visionSample) {
          rawDist = SmartDashboard.getNumber("visionRawDist", 0.0);
          rawAngle = SmartDashboard.getNumber("visionRawAngle", 0.0);
          visionSample = sdVisionSample;
          newData = true;
        }
      } else {
        if (newData) {
          rawDist = 25.0 / Math.sqrt(visionArea);
          rawAngle = 10.0*(visionCenter/camWidth - 1.0) ;
        }
      }
      if (newData) {
        newData = false;
        realDist = distM * rawDist + distC;
        realAngle = angleM * rawAngle + angleC;
        return true;
      } else {
        return false;
      }
    }
  }



  public double getTargetDistance() {
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
    if (! RobotMap.visionPi) {
      SmartDashboard.putNumber("Vision rawDist", rawDist);
      SmartDashboard.putNumber("Vision rawAngle", rawAngle);
      SmartDashboard.putNumber("Vision Area", visionArea);
      SmartDashboard.putNumber("Vision Center", visionCenter);
    }
    
  }
}
