/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.Flywheel;
import frc.robot.subsystems.Hopper;
import java.util.List;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import frc.robot.commands.AutonRoutine;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.util.Limelight;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Intake.IntakeState;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  public static Flywheel flywheel;
  public static Drivetrain drivetrain;
  public static Intake intake;
  public static Hopper hopper;
  public static Climber climber;
  public static OI oi;
  //private RobotContainer m_robotContainer;

  public enum RobotState{
    DISABLED, TELEOP, AUTON
  };

  private static RobotState currState = RobotState.DISABLED;
  private RamseteCommand autoCommand;
  private double startDisabledTime;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    flywheel =  new Flywheel();
    drivetrain = new Drivetrain();
    intake = new Intake();
    hopper = new Hopper();
    climber = new Climber();
    Hardware.limelight = new Limelight();
    oi = new OI();

    setRobotState(RobotState.DISABLED);
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    flywheel.log();
    drivetrain.log();
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
    setRobotState(RobotState.DISABLED);
    startDisabledTime = Timer.getFPGATimestamp();
    drivetrain.configNeutralMode(NeutralMode.Brake);
  }

  @Override
  public void disabledPeriodic() {
    if(Timer.getFPGATimestamp() - startDisabledTime > 2)
      drivetrain.configNeutralMode(NeutralMode.Coast);
  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    //m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    drivetrain.setPathDirection(false);
    Trajectory traj1 = TrajectoryGenerator.generateTrajectory(List.of(
      new Pose2d(),
      new Pose2d(5.3,1, new Rotation2d())

    ), drivetrain.getTrajectoryConfig());
   
    drivetrain.setPathDirection(true);

    Trajectory traj2 = TrajectoryGenerator.generateTrajectory(List.of(
    new Pose2d(2, 1, new Rotation2d()),
    new Pose2d()

    ),drivetrain.getTrajectoryConfig());
    
  
    drivetrain.resetOdometry();
    m_autonomousCommand = new AutonRoutine(generatePath(traj1), generatePath(traj2));
    setRobotState(RobotState.AUTON);
    drivetrain.configNeutralMode(NeutralMode.Brake);
    m_autonomousCommand.schedule();
    intake.resetPivotEncoder();
    intake.setIntakeState(IntakeState.STOWED);

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    setRobotState(RobotState.TELEOP);
    drivetrain.configNeutralMode(NeutralMode.Coast);
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
  public void setRobotState(final RobotState newState) {
    currState = newState;
  }

  public static RobotState getRobotState(){
    return currState;
  }

  
  public Command generatePath(Trajectory trajectory){
    
     autoCommand = new RamseteCommand(
      trajectory,
      drivetrain::getPose,
      drivetrain.getRamseteController(),
      drivetrain.getFeedForward(),
      drivetrain.getDriveKinematics(),
      drivetrain::getWheelSpeeds,
      drivetrain.getLeftDriveController(),
      drivetrain.getRightDriveController(),
      drivetrain::setOutputVolts,
      drivetrain
    );

  
    return autoCommand;
  }

}
