
package org.frc1721.steamworks;

import org.frc1721.steamworks.subsystems.*;
import org.frc1721.steamworks.PreferencesNames;
import org.frc1721.steamworks.commands.*;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class Robot extends IterativeRobot {

	public static OI oi;
	
	public static DigitalInput limitSwitch;
	
	/** 
	 * Subsystems List
	 * In order to create a new subsystem this list must be appended.
	 * Also note that you must initialize your subsystem in robotInit() 
	 */
	public static Climber climber;
	public static Shooter shooter;
	public static DriveTrain driveTrain;
	public static CustomRobotDrive robotDrive;
	public static NavxController navController;
	public static Preferences preferences;
	public static CommandGroup autonomousCommand;
	public static SendableChooser autoChooser;
	public static DistanceController distanceController;
	
	@Override
	public void robotInit() {
		/* Initialize the Drive Train systems */
		
		// Motor Controllers
		RobotMap.dtLeft = new Victor(RobotMap.dtlPWM);
		RobotMap.dtRight = new Victor(RobotMap.dtrPWM);
		//RobotMap.dtRight.setInverted(true);
		// Encoders
		RobotMap.dtlEnc = new Encoder(RobotMap.dtlEncPA, RobotMap.dtlEncPB, RobotMap.dtrEncL);
		RobotMap.dtrEnc = new Encoder(RobotMap.dtrEncPA, RobotMap.dtrEncPB, RobotMap.dtlEncR);
		RobotMap.dtlEnc.setDistancePerPulse(0.0074536447630841);
		RobotMap.dtrEnc.setDistancePerPulse(0.0074074074074074); // TODO, move to RobotMap
		
		// PID Controllers
		RobotMap.dtLeftController = new CustomPIDController(RobotMap.dtP, RobotMap.dtI, 
				RobotMap.dtD, RobotMap.dtF, RobotMap.dtlEnc, RobotMap.dtLeft, 0.01);
		RobotMap.dtRightController = new CustomPIDController(RobotMap.dtP, RobotMap.dtI, 
				RobotMap.dtD, RobotMap.dtF, RobotMap.dtrEnc, RobotMap.dtRight, 0.01);
		RobotMap.dtLeftController.setPIDSourceType(PIDSourceType.kRate);
		RobotMap.dtRightController.setPIDSourceType(PIDSourceType.kRate);
		

		//robotDrive.setInvertedMotor(robotDrive.MotorType.kFrontRight, true);
		
		// Gyro and controller
        RobotMap.navx = new AHRS(SPI.Port.kMXP, RobotMap.navUpdateHz); 
        navController = new NavxController("HeadingController", RobotMap.navP, RobotMap.navI, RobotMap.navD,
        		RobotMap.navF, RobotMap.navx, PIDSourceType.kDisplacement);
        // Add the drive train last since it depends on robotDrive and navController
		//Drive System
		robotDrive = new CustomRobotDrive(RobotMap.dtLeft, RobotMap.dtRight,
						RobotMap.dtLeftController, RobotMap.dtRightController,
						navController);
		//robotDrive.stopMotors();
        
        driveTrain = new DriveTrain(robotDrive, navController);
        driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
        driveTrain.setDriveScale(RobotMap.driveRateScale, RobotMap.turnRateScale);
        
		/* Add items to live windows */
        LiveWindow.addSensor("Gyro", "navx", RobotMap.navx);
		LiveWindow.addActuator("LeftRobotDrive", "Victor", RobotMap.dtLeft);
		LiveWindow.addActuator("RightRobotDrive", "Victor", RobotMap.dtRight);
		LiveWindow.addSensor("LeftRobotDrive", "Encoder", RobotMap.dtlEnc);
	    LiveWindow.addSensor("RightRobotDrive", "Encoder", RobotMap.dtrEnc);
	    LiveWindow.addActuator("LeftRobotDrive", "Controller", RobotMap.dtLeftController);
	    LiveWindow.addActuator("RightRobotDrive", "Controller", RobotMap.dtRightController);
	    
	    limitSwitch = new DigitalInput(RobotMap.lsLsPA);
	    
	    // Add the distance controller
	    distanceController = new DistanceController("DistanceController", RobotMap.distP, 
	    		RobotMap.distI, RobotMap.distD, driveTrain);

	    // Create preferences
        preferences =Preferences.getInstance();
        // Reset the preferences to the default values
        setPreferences();
        
		// Create a chooser for auto so it can be set from the DS
		autonomousCommand = new TestAuto();
		autoChooser = new SendableChooser();
		autoChooser.addDefault("Test", new TestAuto());
		SmartDashboard.putData("Auto Chooser", autoChooser);        
        
//	    new Thread(() -> {
//            UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
//            camera.setResolution(640, 480);
//            
//            CvSink cvSink = CameraServer.getInstance().getVideo();
//            CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
//            
//            Mat source = new Mat();
//            Mat output = new Mat();
//            
//            while(!Thread.interrupted()) {
//                cvSink.grabFrame(source);
//                Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
//                outputStream.putFrame(output);
//            }
//        }).start();
	    
	    // Create the OI
	    oi = new OI();
	}

	@Override
	public void disabledInit() {
	}

	@Override
	public void disabledPeriodic() {
		getPreferences();
	}

	@Override
	public void autonomousInit() {
		robotDrive.enablePID(); // TODO Make enablePID reset gyro so the robot doesn't spin
		// Gyro is only reset when the  mode changes, so shut the it off then back on in case teleop
		// is started multiple times.
		RobotMap.navx.zeroYaw();
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
    	autonomousCommand = (CommandGroup) autoChooser.getSelected();
    	//autonomousCommand.addCommands();
    	autonomousCommand.start();
	}

	@Override
	public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        updateSmartDashboard();
	}

	@Override
	public void teleopInit() {
		
		
		robotDrive.enablePID(); // TODO Make enablePID reset gyro so the robot doesn't spin
		// Gyro is only reset when the  mode changes, so shut the it off then back on in case teleop
		// is started multiple times.
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.rate);
	}
	
	public void updateSmartDashboard() {
		LiveWindow.run();
		SmartDashboard.putNumber("Yaw", RobotMap.navx.getYaw());
//		SmartDashboard.putNumber("Angle", RobotMap.navx.getAngle());
//		SmartDashboard.putNumber("CompassHeading", RobotMap.navx.getCompassHeading());
//		SmartDashboard.putNumber("Altitude", RobotMap.navx.getAltitude());
//		SmartDashboard.putNumber("DisplacementX", RobotMap.navx.getDisplacementX());
//		SmartDashboard.putNumber("DisplacementY", RobotMap.navx.getDisplacementY());
//		SmartDashboard.putNumber("DisplacementZ", RobotMap.navx.getDisplacementZ());
//		SmartDashboard.putNumber("Roll", RobotMap.navx.getRoll());
				
		
		SmartDashboard.putNumber("Joystick One YAxis", OI.jsticks[0].getY());
		SmartDashboard.putNumber("Joystick One Twist", OI.jsticks[0].getTwist());		
		
		SmartDashboard.putBoolean("PID", Robot.robotDrive.getPIDStatus());
		SmartDashboard.putNumber("DriveDistance", Robot.robotDrive.getDistance());
	}
	
	
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		updateSmartDashboard();
	}

	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
	
	
	public static void getPreferences() {
		RobotMap.navRateP = preferences.getDouble(PreferencesNames.GYRO_RATE_P, RobotMap.navRateP);
		RobotMap.navRateD = preferences.getDouble(PreferencesNames.GYRO_RATE_D, RobotMap.navRateD);		
		RobotMap.navRateF = preferences.getDouble(PreferencesNames.GYRO_RATE_F, RobotMap.navRateF);
		RobotMap.navP = preferences.getDouble(PreferencesNames.GYRO_HEADING_P, RobotMap.navP);
		RobotMap.navI = preferences.getDouble(PreferencesNames.GYRO_HEADING_D, RobotMap.navD);		
		RobotMap.navD = preferences.getDouble(PreferencesNames.GYRO_HEADING_I, RobotMap.navI);
	}

	public static void setPreferences() {
		preferences.putDouble(PreferencesNames.GYRO_HEADING_P, RobotMap.navP);
		preferences.putDouble(PreferencesNames.GYRO_HEADING_I, RobotMap.navI);
	    preferences.putDouble(PreferencesNames.GYRO_HEADING_D, RobotMap.navD);
		preferences.putDouble(PreferencesNames.GYRO_RATE_P, RobotMap.navRateP);
		preferences.putDouble(PreferencesNames.GYRO_RATE_D, RobotMap.navRateD);		
		preferences.putDouble(PreferencesNames.GYRO_RATE_F, RobotMap.navRateF);

	}
	
}
