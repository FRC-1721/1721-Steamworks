package org.frc1721.steamworks;
//@formatter:off

import java.util.TimerTask;

import org.frc1721.steamworks.RobotMap;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.kauailabs.navx.frc.AHRS;

public class PositionEstimator {

	public class FieldTarget {
		public double[]	position;
		public double[]	normal;

		FieldTarget() {
			position = new double[2];
			normal = new double[2];
		}

		public void set(double X, double Y, double angle) {
			position[0] = X;
			position[1] = Y;
			normal[0] = Math.cos(Math.toRadians(angle));
			normal[1] = Math.sin(Math.toRadians(angle));
		}



		boolean inCone(double X, double Y, double angle) {
			double[] delX = new double[2];
			delX[0] = X - position[0];
			delX[1] = Y - position[1];
			double dotP = delX[0] * normal[0] + delX[1] * normal[1];
			// Check if it's on the correct side of the target
			if (dotP > 0.0) {
				double dist = Math.sqrt(delX[0] * delX[0] + delX[1] * delX[1]);
				if ((dotP / dist) > Math.cos(Math.toRadians(angle))) {
					return true;
				}
			}
			return false;
		}

		public double[] getApproachPoint(double dist) {
			double[] pt = new double[2];
			pt[0] = position[0] + normal[0] * dist;
			pt[1] = position[1] + normal[1] * dist;
			return pt;
		}

		public double[] getPositionFromHeading(double dist, double heading) {

			double[] pt = new double[2];
			// Heading provided is the heading to the target, so add 180.0 to get the
			// heading from the target to the robot
			double rAngle = Math.toRadians(heading + 180.0);
			pt[0] = position[0] + Math.cos(rAngle) * dist;
			pt[1] = position[1] + Math.sin(rAngle) * dist;
			return pt;
		}

	}

	private static FieldTarget[]	gearTargets;
	private static FieldTarget		curTarget;
	public static final double		kDefaultPeriod		= .05;
	public static final double		kGravity			= 32.174;
	// ToDo get actual wheel base
	private static final double		kWheelBase			= 4.0;
	private double					m_period			= kDefaultPeriod;
	java.util.Timer					m_peLoop;
	Timer							m_resetTimer;
	private double					lastVelEst[]		= new double[2];
	private double					lastPosEst[]		= new double[2];
	private double					lastEncS[]			= new double[2];
	private double					lastHeading			= 0.0;
	private double					lastAccelEst[]		= new double[2];
	private Encoder					m_ltEncoder;
	private Encoder					m_rtEncoder;
	private AHRS					m_navx;
	private double					encGain				= 1.0;
	private double					gyroGain			= 0.0;
	private double					deltaT				= 0.0;
	private boolean					collisionDetected	= false;
	private float					lastAccelX			= 0;
	private float					lastAccelY			= 0;
	private static float			kCollisionThreshold	= 0.5F;
	private static float			maxJerk				= 0;
	public double					visionPosition[]	= new double[2];


	public PositionEstimator(double period) {

		m_ltEncoder = RobotMap.dtlEnc;
		m_rtEncoder = RobotMap.dtrEnc;
		m_navx = RobotMap.navx;
		// Setup gear targets
		gearTargets = new FieldTarget[3];
		for (int i = 0; i < 3; i++) {
			gearTargets[i] = new FieldTarget();
		}
		gearTargets[0].set(RobotMap.cGearDepositX, RobotMap.cGearDepositY, 180.0);
		gearTargets[1].set(RobotMap.sideGearDepositX, RobotMap.sideGearDepositY, 120.0);
		gearTargets[2].set(RobotMap.sideGearDepositX, -RobotMap.sideGearDepositY, -120.0);
		curTarget = gearTargets[0];

		m_peLoop = new java.util.Timer();
		m_resetTimer = new Timer();
		m_resetTimer.start();

		m_period = period;
		// Initialize all arrays
		for (int i = 0; i < 2; i++) {
			lastVelEst[i] = 0.0;
			lastPosEst[i] = 0.0;
			lastAccelEst[i] = 0.0;
			lastEncS[0] = m_ltEncoder.getDistance();
			lastEncS[1] = -m_rtEncoder.getDistance();
		}
		m_peLoop.schedule(new PositionEstimatorTask(this), 0L, (long) (m_period * 1000));
		visionPosition[0] = 0.0;
		visionPosition[1] = 0.0;
	}

	public PositionEstimator() {
		this(kDefaultPeriod);
	}


	private class PositionEstimatorTask extends TimerTask {

		private PositionEstimator m_positionEstimator;

		public PositionEstimatorTask(PositionEstimator positionEstimator) {
			if (positionEstimator == null) {
				throw new NullPointerException("Given Position Estimator was null");
			}
			m_positionEstimator = positionEstimator;
		}

		@Override
		public void run() {
			m_positionEstimator.calculate();
		}
	}

	private void filteredEncoderVelocity(double deltaHeading, double vel[]) {
		double encS[] = new double[2];
		encS[0] = m_ltEncoder.getDistance();
		encS[1] = -m_rtEncoder.getDistance();
		if (RobotMap.leftEncoderDisabled) {
			encS[0] = encS[1];
		} else if (RobotMap.rightEncoderDisabled) {
			encS[1] = encS[0];
		}
		vel[0] = (encS[0] - lastEncS[0]);
		vel[1] = (encS[1] - lastEncS[1]);
		lastEncS[0] = encS[0];
		lastEncS[1] = encS[1];

		// Transform to field relative directions
		double dSdT = 0.5 * (vel[0] + vel[1]) / deltaT;
		vel[0] = Math.cos(lastHeading) * dSdT;
		vel[1] = Math.sin(lastHeading) * dSdT;
	}

	private double getHeading() {
		return Math.toRadians(m_navx.getYaw() - RobotMap.yawOffset);
	}


	private void calculate() {
		// Don't start taking data until calibration is done
		deltaT = m_resetTimer.get();
		m_resetTimer.reset();
		if (m_navx.isCalibrating()) {
			m_resetTimer.reset();
			return;
		}
		detectCollision();
		synchronized (this) {
			// Treate the gyro heading as gospel
			double curHeading = getHeading();

			// Calculate Encoders measurements

			double velEnc[] = new double[2];
			filteredEncoderVelocity(curHeading - lastHeading, velEnc);

			double posEnc[] = new double[2];
			for (int i = 0; i < 2; i++) {
				posEnc[i] = lastPosEst[i] + velEnc[i] * deltaT;
			}

			// Calculate Gyro's measurements
			// Need to handle the sign properly
			double accelGyro[] = new double[2];
			accelGyro[0] = m_navx.getWorldLinearAccelX() * kGravity;
			accelGyro[1] = m_navx.getWorldLinearAccelY() * kGravity;



			// Calculate the estimated quantities assuming no other changes
			double posEst[] = new double[2];
			double velEst[] = new double[2];
			double accelEst[] = new double[2];
			for (int i = 0; i < 2; i++) {
				accelEst[i] = lastAccelEst[i];
				velEst[i] = lastVelEst[i] + lastAccelEst[i] * deltaT;
				posEst[i] = lastPosEst[i] + lastVelEst[i] * deltaT + 0.5 * deltaT * deltaT * lastAccelEst[i];
			}

			// Update the estimates based on the gains
			for (int i = 0; i < 2; i++) {
				// Use gyro directly for accelleration
				accelEst[i] = (1.0 - gyroGain) * accelEst[i] + gyroGain * accelGyro[i];
				// Use encoders with gain to adjust the velocity and prevent velocity drift
				velEst[i] = velEst[i] + encGain * (velEnc[i] - velEst[i]);
				// Adjust position with the encoders
				posEst[i] = posEst[i] + encGain * (posEnc[i] - posEst[i]);
				lastAccelEst[i] = accelEst[i];
				lastVelEst[i] = velEst[i];
				lastPosEst[i] = posEst[i];

				// Update the gains

			}

			// Update the "last" values
			lastHeading = curHeading;
		}
	}

	// Find the gear target that is in a 30 degree cone
	public boolean setBestGearTarget() {
		for (int i = 0; i < 3; i++) {
			if (gearTargets[i].inCone(lastPosEst[0], lastPosEst[1], 45.0)) {
				curTarget = gearTargets[i];
				return true;
			}
		}
		return false;
	}

	public double[] getTargetApproachPoint(double dist) {
		return curTarget.getApproachPoint(dist);
	}

	public void setPosition(double x, double y) {
		lastPosEst[0] = x;
		lastPosEst[1] = y;
	}

	public void zeroVelocity(double gain) {
		lastVelEst[0] = (1.0 - gain) * lastVelEst[0];
		lastVelEst[1] = (1.0 - gain) * lastVelEst[1];
	}

	public double getVelocityX() {
		return lastVelEst[0];
	}

	public double getVelocityY() {
		return lastVelEst[1];
	}

	public double getVelocityZ() {
		return 0;
	}

	public double getDisplacementX() {
		return lastPosEst[0];
	}

	public double getDisplacementY() {
		return lastPosEst[1];
	}

	public double getAccelX() {
		return lastAccelEst[0];
	}

	public double getAccelY() {
		return lastAccelEst[1];
	}

	public double getDisplacementZ() {
		return 0;
	}

	public double getDistanceFromPoint(double x, double y) {
		double dx = getDisplacementX() - x;
		double dy = getDisplacementY() - y;
		double distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}

	public double getHeadingToPoint(double x, double y, boolean relHeading) {
		double dx = getDisplacementX() - x;
		double dy = getDisplacementY() - y;
		double heading = Math.toDegrees(Math.atan2(dy, dx));
		if (relHeading)
			heading -= getHeading() + 180.0; // Camera is mounted on the back, so add 180.0
		if (heading > 180.0) {
			heading -= 360.0;
		} else if (heading < -180.0) {
			heading += 360.0;
		}
		return heading;
	}

	public void updatePositionFromVision(double dist, double heading) {
		// Only update if we are in range of a valid target
		if (dist < 2.0) return;
		if (setBestGearTarget()) {
			double[] newPosition = curTarget.getPositionFromHeading(dist, heading);

			for (int i = 0; i < 2; i++) {
				visionPosition[i] = newPosition[i];
				// Only take 10% of the correction due to sensor error
				if (Robot.cameraSystem.calibrated) {
					double delX = 0.5 * (newPosition[i] - lastPosEst[i]);
					// limit the correction in case of bad data. At 10 fps this can get bad quickly.
					if (delX > 0.5) {
						delX = 0.5;
					} else if (delX < -0.5) {
						delX = -0.5;
					} 
					lastPosEst[i] = lastPosEst[i] + delX;
				}
			}
		}
	}

	private void detectCollision() {
		collisionDetected = false;

		float accelX = RobotMap.navx.getWorldLinearAccelX();
		float jerkX = Math.abs(accelX - lastAccelX);
		lastAccelX = accelX;
		float accelY = RobotMap.navx.getWorldLinearAccelY();
		float jerkY = Math.abs(accelY - lastAccelY);
		lastAccelY = accelY;
		maxJerk = jerkX;
		if (jerkY > maxJerk)
			maxJerk = jerkY;
		if (maxJerk > kCollisionThreshold)
			collisionDetected = true;

	}

	public boolean checkCollision() {
		return collisionDetected;
	}

	public void updateSmartDashboard() {
		// Left side
		// SmartDashboard.putBoolean("PositionEstCalibrating", m_navx.isCalibrating());
		SmartDashboard.putNumber("PositionEstX", getDisplacementX());
		SmartDashboard.putNumber("PositionEstY", getDisplacementY());
		SmartDashboard.putNumber("VisionEstX", visionPosition[0]);
		SmartDashboard.putNumber("VisionEstY", visionPosition[1]);
		SmartDashboard.putNumber("PositionEstJerk", maxJerk);
		// SmartDashboard.putNumber("PositionEstVelY", getVelocityY());
		// SmartDashboard.putNumber("PositionEstAccelX", lastAccelEst[0]);
		// SmartDashboard.putNumber("PositionEstAccelY", lastAccelEst[1]);
		// SmartDashboard.putNumber("PositionEstDeltaT", deltaT);
		// SmartDashboard.putNumber("PositionEstYaw", m_navx.getYaw());
	}

}
