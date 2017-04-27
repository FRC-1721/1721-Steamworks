package org.frc1721.steamworks;

import org.frc1721.steamworks.commands.AutoCalibrateVision;
import org.frc1721.steamworks.commands.AutoCrossLineStraight;
import org.frc1721.steamworks.commands.AutoDepositGear;
import org.frc1721.steamworks.commands.AutoDepositGearSides;
import org.frc1721.steamworks.commands.AutoTestVision;
import org.frc1721.steamworks.commands.DoNothing;
import org.frc1721.steamworks.commands.TestAuto;
import org.frc1721.steamworks.subsystems.CameraSystem;
import org.frc1721.steamworks.subsystems.ClimberController;
import org.frc1721.steamworks.subsystems.DistanceController;
import org.frc1721.steamworks.subsystems.DriveTrain;
import org.frc1721.steamworks.subsystems.LCDController;
import org.frc1721.steamworks.subsystems.NavxController;
import org.frc1721.steamworks.subsystems.ShooterController;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.hal.AllianceStationID;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	public static OI oi;

	/**
	 * Subsystems List In order to create a new subsystem this list must be
	 * appended. Also note that you must initialize your subsystem in
	 * robotInit()
	 */

	public static ClimberController climber;
	public static ShooterController		shooter;
	public static DriveTrain			driveTrain;
	public static CustomRobotDrive		robotDrive;
	public static NavxController		navController;
	public static LCDController			lcdController;
	public static CommandGroup			autonomousCommand;
	@SuppressWarnings("rawtypes")
	public static SendableChooser		autoChooser;
	public static DistanceController	distanceController;
	public static PositionEstimator		positionEstimator;
	public static DigitalInput			topLimitSwitch;
	public static DigitalInput			bottomLimitSwitch;
	public static DigitalInput			gearLimitSwitch;
	public static CameraSystem			cameraSystem;

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	/** Initialize the Drive Train systems **/
	public void robotInit() {

		/** Motor Controllers **/
		RobotMap.dtLeft = new VictorSP(RobotMap.dtlPWM);
		RobotMap.dtRight = new VictorSP(RobotMap.dtrPWM);
		RobotMap.cClimb = new VictorSP(RobotMap.climbPWM);
		RobotMap.sShooter = new VictorSP(RobotMap.shooterPWM);

		/** Motor Controller Inversions **/
		RobotMap.cClimb.setInverted(RobotMap.climbInverted);
		RobotMap.sShooter.setInverted(RobotMap.shooterInverted);

		/** Encoders **/
		RobotMap.dtlEnc = new Encoder(RobotMap.dtlEncPA, RobotMap.dtlEncPB, RobotMap.dtrEncL);
		RobotMap.dtrEnc = new Encoder(RobotMap.dtrEncPA, RobotMap.dtrEncPB, RobotMap.dtlEncR);
		RobotMap.dtlEnc.setDistancePerPulse(RobotMap.lDPP);
		RobotMap.dtrEnc.setDistancePerPulse(RobotMap.rDPP);

		RobotMap.shooterEnc = new Encoder(RobotMap.sEncPA, RobotMap.sEncPB, RobotMap.sEncR);
		RobotMap.shooterEnc.setDistancePerPulse(RobotMap.sDPP);

		/** Network Tables **/
		// RobotMap.cameraTable = NetworkTable.getTable("GRIP/myContourReport");

		/** PID Controllers **/
		RobotMap.dtLeftController = new CustomPIDController(RobotMap.dtP, RobotMap.dtI, RobotMap.dtD, RobotMap.dtF,
				RobotMap.dtlEnc, RobotMap.dtLeft, 0.01);
		RobotMap.dtRightController = new CustomPIDController(RobotMap.dtP, RobotMap.dtI, RobotMap.dtD, RobotMap.dtF,
				RobotMap.dtrEnc, RobotMap.dtRight, 0.01);
		RobotMap.dtLeftController.setPIDSourceType(PIDSourceType.kRate);
		RobotMap.dtRightController.setPIDSourceType(PIDSourceType.kRate);

		/** LCD **/
		// RobotMap.lcd = new I2C(I2C.Port.kOnboard, 0x27);
		// lcdController = new LCDController();
		// lcdController.initLCD(RobotMap.lcd);
		// LCDController.print(RobotMap.lcd, "Hello World", 1);

		/** Limit Switch's **/
		// topLimitSwitch = new DigitalInput(RobotMap.topLs);
		// bottomLimitSwitch = new DigitalInput(RobotMap.bottomLs); // TODO remove limit switch stuffs
		// gearLimitSwitch = new DigitalInput(RobotMap.gearLs);

		/** Gyro and navController **/
		RobotMap.navx = new AHRS(SPI.Port.kMXP, RobotMap.navUpdateHz);
		navController = new NavxController("HeadingController", RobotMap.navP, RobotMap.navI, RobotMap.navD,
				RobotMap.navF, RobotMap.navx, PIDSourceType.kDisplacement);
		navController.setDisplacementRange(-180.0, 180.0);
		positionEstimator = new PositionEstimator();

		/** Shooter **/
		shooter = new ShooterController();

		/** Climber **/
		climber = new ClimberController();

		/** Servo **/
		RobotMap.shooterServo = new Servo(RobotMap.shooterServoPWN);

		/** Robot Drive **/
		// robotDrive.setInvertedMotor(robotDrive.MotorType.kFrontRight, true);
		robotDrive = new CustomRobotDrive(RobotMap.dtLeft, RobotMap.dtRight, RobotMap.dtLeftController,
				RobotMap.dtRightController, navController);
		// robotDrive.stopMotors();

		/** Drive Train **/
		// Add the drive train last since it depends on robotDrive and
		driveTrain = new DriveTrain(robotDrive, navController);
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
		driveTrain.setDriveScale(RobotMap.driveRateScale, RobotMap.turnRateScale);

		cameraSystem = new CameraSystem();

		/** Auto Chooser **/
		// Create a chooser for auto so it can be set from the DS
		autonomousCommand = new TestAuto();

		// TODO Make this more autonomous
		// E.G. DriverStation.getInstance().getAlliance();
		// E.G. DriverStation.getInstance().getLocation();

		autoChooser = new SendableChooser();
		autoChooser.addDefault("CrossLineStraightRight", new AutoCrossLineStraight(5.5));
		autoChooser.addDefault("CrossLineStraightLeft", new AutoCrossLineStraight(-5.5));
		// center of robot about 2 feet off wall
		autoChooser.addObject("TestVision", new AutoTestVision());
//		autoChooser.addObject("AutoGearRight", new AutoDepositGear(1.0));
//		autoChooser.addObject("AutoGearLeft", new AutoDepositGear(-1.0));
		autoChooser.addObject("AutoGearStraight", new AutoDepositGear(0.0));
		autoChooser.addObject("SideAutoGearRightShoot", new AutoDepositGearSides(1.0, true));
		autoChooser.addObject("SideAutoGearLeftShoot", new AutoDepositGearSides(-1.0, true));
		autoChooser.addObject("SideAutoGearRight", new AutoDepositGearSides(1.0, false));
		autoChooser.addObject("SideAutoGearLeft", new AutoDepositGearSides(-1.0, false));
		// autoChooser.addObject("DepositSteam10Red", new AutoDepositSteam(2.0, -9.5, RobotMap.redTeam, true));
		autoChooser.addObject("CalibrateVision", new AutoCalibrateVision());
		autoChooser.addObject("Do Nothing", new DoNothing());
		/*
		 * autoChooser.addObject("Steam10Blue", new AutoDepositSteam(2.0, 10.0, RobotMap.blueTeam, false));
		 * autoChooser.addObject("Steam15Blue", new AutoDepositSteam(2.0, 15.0, RobotMap.blueTeam, false));
		 * autoChooser.addObject("DepositSteam10Blue", new AutoDepositSteam(2.0, 9.5, RobotMap.blueTeam, true));
		 * autoChooser.addObject("DepositSteam15Blue", new AutoDepositSteam(2.0, 13.5, RobotMap.blueTeam, true));
		 */
		SmartDashboard.putData("Auto Chooser", autoChooser);

		/** Live Window **/
		/* Add items to live windows */
		LiveWindow.addSensor("Gyro", "navx", RobotMap.navx);
		LiveWindow.addActuator("LeftRobotDrive", "Victor", RobotMap.dtLeft);
		LiveWindow.addActuator("RightRobotDrive", "Victor", RobotMap.dtRight);
		LiveWindow.addSensor("LeftRobotDrive", "Encoder", RobotMap.dtlEnc);
		LiveWindow.addSensor("RightRobotDrive", "Encoder", RobotMap.dtrEnc);
		LiveWindow.addActuator("LeftRobotDrive", "Controller", RobotMap.dtLeftController);
		LiveWindow.addActuator("RightRobotDrive", "Controller", RobotMap.dtRightController);

		/** Distance Controller **/
		distanceController = new DistanceController("DistanceController", RobotMap.distP, RobotMap.distI,
				RobotMap.distD, driveTrain);

		// new Thread(() -> {
		// UsbCamera camera =
		// CameraServer.getInstance().startAutomaticCapture();
		// camera.setResolution(640, 480);
		//
		// CvSink cvSink = CameraServer.getInstance().getVideo();
		// CvSource outputStream = CameraServer.getInstance().putVideo("Blur",
		// 640, 480);
		//
		// Mat source = new Mat();
		// Mat output = new Mat();
		//
		// while (!Thread.interrupted()) {
		// cvSink.grabFrame(source);
		// Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2);
		// outputStream.putFrame(output);
		// }
		// }).start();


		/** Create the OI **/
		oi = new OI();
		SmartDashboard.putString("robotMode", "disabled");
	}

	@Override
	public void disabledInit() {
		SmartDashboard.putString("robotMode", "disabled");
	}

	@Override
	public void disabledPeriodic() {
		// cameraSystem.processData();
		SmartDashboard.putString("robotMode", "disabled");
	}

	@Override
	public void autonomousInit() {
		SmartDashboard.putString("robotMode", "auto");
		robotDrive.enablePID();

		/*
		 * Gyro is only reset when the mode changes, so shut the it off then
		 * back on in case auto is started multiple times.
		 */

		RobotMap.navx.zeroYaw();
		positionEstimator.setPosition(RobotMap.xStart, RobotMap.yStart);
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
		autonomousCommand = (CommandGroup) autoChooser.getSelected();
		// autonomousCommand.addCommands();
		autonomousCommand.start();
	}

	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		// printSmartDashboard();
	}

	@Override
	public void teleopInit() {
		SmartDashboard.putString("robotMode", "teleop");
		robotDrive.disablePID();
		// robotDrive.enablePID();
		/*
		 * Gyro is only reset when the mode changes, so shut the it off then
		 * back on in case teleop is started multiple times.
		 */
		driveTrain.setGyroMode(CustomRobotDrive.GyroMode.off);
		// driveTrain.setGyroMode(CustomRobotDrive.GyroMode.rate);
		
		if (autonomousCommand != null)
		    autonomousCommand.cancel();
	}

	@Override
	public void robotPeriodic() {
		// cameraSystem.processData();
		printSmartDashboard();

	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		LiveWindow.run();
	}

	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}

	private void printSmartDashboard() {
		// out.printf("'%s.printSmartDashboard()' Worked.\n",
		// this.getClass().getName());

		SmartDashboard.putNumber("Left Drive Train Encoder", RobotMap.dtlEnc.get());
		SmartDashboard.putNumber("Right Drive Train Encoder", RobotMap.dtrEnc.get());
		
		
		/** Limit Switch Stuff **/
		// SmartDashboard.putBoolean("Gear Limit Switch", gearLimitSwitch.get());
		// SmartDashboard.putBoolean("Top Limit Switch", topLimitSwitch.get());
		// SmartDashboard.putBoolean("Bottom Limit Switch", bottomLimitSwitch.get());

		/** Navx Stuff **/
		SmartDashboard.putNumber("Yaw", RobotMap.navx.getYaw());
		navController.updateSmartDashboard();
		distanceController.updateSmartDashboard();
		positionEstimator.updateSmartDashboard();
		// SmartDashboard.putNumber("Angle",RobotMap.navx.getAngle());
		// SmartDashboard.putNumber("CompassHeading",RobotMap.navx.getCompassHeading());
		// SmartDashboard.putNumber("Altitude",RobotMap.navx.getAltitude());
		// SmartDashboard.putNumber("DisplacementX",RobotMap.navx.getDisplacementX());
		// SmartDashboard.putNumber("DisplacementY",RobotMap.navx.getDisplacementY());
		// SmartDashboard.putNumber("DisplacementZ",RobotMap.navx.getDisplacementZ());
		// SmartDashboard.putNumber("Roll",RobotMap.navx.getRoll());

		/** Camera Data **/
		cameraSystem.updateSmartDashboard();
		ShooterController.updateSmartDashboard();
		/*
		 * SmartDashboard.putNumber("area", RobotMap.cameraTable.getNumberArray("area", new double[] { -1 })[0]);
		 * SmartDashboard.putNumber("centerY", RobotMap.cameraTable.getNumberArray("centerY", new double[] { -1 })[0]);
		 * SmartDashboard.putNumber("centerX", RobotMap.cameraTable.getNumberArray("centerX", new double[] { -1 })[0]);
		 * SmartDashboard.putNumber("height", RobotMap.cameraTable.getNumberArray("height", new double[] { -1 })[0]);
		 * SmartDashboard.putNumber("width", RobotMap.cameraTable.getNumberArray("width", new double[] { -1 })[0]);
		 * SmartDashboard.putNumber("solidity", RobotMap.cameraTable.getNumberArray("solidity", new double[] { -1 })[0]);
		 */
		/** Controller Stuff **/
		SmartDashboard.putNumber("Joystick One YAxis", OI.jsticks[0].getY());
		SmartDashboard.putNumber("Joystick One Twist", OI.jsticks[0].getTwist());

		SmartDashboard.putNumber("Operator LY Axis", OI.jOp.getRawAxis(RobotMap.gamepadLYaxis));
		SmartDashboard.putNumber("Operator Left Trigger", OI.jOp.getRawAxis(RobotMap.gamepadLTrigger));
		SmartDashboard.putNumber("Operator Right Trigger", OI.jOp.getRawAxis(RobotMap.gamepadRTrigger));


		/** PID Stuff **/
		SmartDashboard.putBoolean("PID", Robot.robotDrive.getPIDStatus());



	}
}
