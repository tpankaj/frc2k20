/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Hardware;
import frc.robot.util.RollingAverage;

public class Flywheel extends SubsystemBase {
  /**
   * Creates a new Flywheel.
   */
  public enum FlywheelState {
    OFF, SPINNINGUP, ATSPEED;
  }

  private FlywheelState currState;
  private double targetVelocity;
  private RollingAverage flywheelRPM;

  double time1, time2;

  public Flywheel() {

    if(Constants.kCompBot){
      Hardware.flywheelMaster = new TalonFX(11);
      Hardware.flywheelFollower = new TalonFX(12);
    }else{
      Hardware.flywheelMaster = new TalonFX(11);
      Hardware.flywheelFollower = new TalonFX(12);
    }


    Hardware.flywheelMaster.configFactoryDefault();
    Hardware.flywheelFollower.configFactoryDefault();
    
    Hardware.flywheelMaster.configVoltageCompSaturation(10, Constants.kTimeoutMs);
    Hardware.flywheelMaster.enableVoltageCompensation(true);

    Hardware.flywheelFollower.follow(Hardware.flywheelMaster);

    Hardware.flywheelMaster.setInverted(false);
    Hardware.flywheelFollower.setInverted(false);

    Hardware.flywheelMaster.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, Constants.kPIDIdx,
        Constants.kTimeoutMs);
    Hardware.flywheelFollower.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, Constants.kPIDIdx,
        Constants.kTimeoutMs);

    Hardware.flywheelMaster.config_kF(Constants.kPIDIdx, Constants.kFlywheelFF);
    Hardware.flywheelMaster.config_kP(Constants.kPIDIdx, Constants.kFlywheelP);
    Hardware.flywheelMaster.config_kD(Constants.kPIDIdx, Constants.kFlywheelD);

    targetVelocity = 0;
    currState = FlywheelState.OFF;

    flywheelRPM = new RollingAverage(5);

    time1 = 0;
    time2 = 0;

  }

  public void setTargetVelocity(double desiredVelocity) {
    targetVelocity = desiredVelocity;
    setFlywheelState(FlywheelState.SPINNINGUP);
  }

  public void updateTargetVelocity(double desiredVelocity) {
    targetVelocity = desiredVelocity;
  }

  public void log() {
     SmartDashboard.putNumber("Flywheel Output",Hardware.flywheelMaster.getMotorOutputPercent());
     SmartDashboard.putNumber("Flywheel Output Followe", Hardware.flywheelFollower.getMotorOutputPercent());
     // SmartDashboard.putNumber("Current Draw",
    // Hardware.flywheelMaster.getStatorCurrent());
     SmartDashboard.putNumber("Wheel RPM Error", targetVelocity - getWheelRPM());
    // SmartDashboard.putNumber("Limelight Ty",
    // Hardware.limelight.getVerticalAngle());
    SmartDashboard.putNumber("Wheel RPM", getWheelRPM());
    SmartDashboard.putNumber("Distance",
        Hardware.limelight.getDistanceFromTarget(Hardware.limelight.getVerticalAngle()));
  }

  public double getWheelRPM() {
    return Hardware.flywheelMaster.getSelectedSensorVelocity() * 600 / 2048 / Constants.kFlywheelGearRatio;
  }

  public void setFlywheelState(FlywheelState newState) {
    currState = newState;

    if (newState == FlywheelState.OFF)
      targetVelocity = 0;
  }

  public FlywheelState getFlywheelState() {
    return currState;
  }

  public void periodic() {
    flywheelRPM.add(getWheelRPM());

    switch (currState) {

    case OFF:
      SmartDashboard.putString("FLYWHEEL STATE", "OFF");
      Hardware.flywheelMaster.set(ControlMode.PercentOutput, 0);

      break;
    case SPINNINGUP:
      SmartDashboard.putString("FLYWHEEL STATE", "Spinning UP");
      Hardware.flywheelMaster.set(ControlMode.Velocity, targetVelocity / 600 * 2048 * Constants.kFlywheelGearRatio);

      if (flywheelRPM.allWithinError(targetVelocity, Constants.kFlywheelAcceptableError)) {
        time1 = Timer.getFPGATimestamp();
        setFlywheelState(FlywheelState.ATSPEED);
      }
      break;
    case ATSPEED:
      SmartDashboard.putString("FLYWHEEL STATE", "AT SPEED");
      Hardware.flywheelMaster.set(ControlMode.Velocity, targetVelocity / 600 * 2048 * Constants.kFlywheelGearRatio);

      if (!flywheelRPM.allWithinError(targetVelocity, Constants.kFlywheelAcceptableError)) {
        time2 = Timer.getFPGATimestamp();
        setFlywheelState(FlywheelState.SPINNINGUP);
      }

      break;
    }

    double recoverTime = time1 - time2;
    SmartDashboard.putNumber("RecoverTime", recoverTime);
  }

  public double getRPMFromDistance(double _distance) {
    return 0;
  }
}
