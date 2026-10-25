package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.SharedClasses.MecanumDrive_Smooth;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;

@TeleOp(name = "Launcher Velocity Test & Smooth Drive", group = "Test")
public class LauncherVelocityTest extends OpMode {

/////////////////////     HARDWARE     /////////////////////
    // Shared hardware class owns all hardwareMap and motor setup.
    RobotHardware robot = new RobotHardware();
    MecanumDrive_Smooth drive;


/////////////////////     VARIABLES     /////////////////////
    // These variables live OUTSIDE loop() so their values survive each loop.

   // START OF Velocity Toggling and Adjustment Variables
    private static final double RPM_STEP = 100.0; // RPM changed per D-pad press
    private static final double MAX_RPM = 6000.0; // Adjustable upper safety limit
    private static final double READY_TOLERANCE = 0.05; // Within 5% of target

    // These values must persist between loop() calls; local loop variables reset
    // on every iteration and would not remember the selected speed or ON/OFF state.
    private double targetRPM = 0.0; //Starting Target RPM
    private boolean launcherOn = false; //Starting Launcher State is Off

    // Remember the PREVIOUS button state to detect a NEW press (rising edge).
    private boolean previousUp = false; //Checks to see if button is still held or was released and then pressed again
    private boolean previousDown = false; //Checks to see if button is still held or was released and then pressed again
    private boolean previousA = false; //Checks to see if button is still held or was released and then pressed again
    // END OF Velocity Toggling and Adjustment Variables

    //Mecanum Drive Variables
    private boolean previousResetButton = false;


    @Override
    //////////////////////// Initialization //////////////////////////////
    public void init() {
        //Initialize Hardware and Mecanum Drive Classes
        robot.init(hardwareMap);
        drive = new MecanumDrive_Smooth(robot);

        //Initialize Ramping value and acceleration values for Mecanum Drive Class
        drive.setRampingEnabled(true); // Set to False if you want to Disable Ramping
        drive.setAccelerationRate(2.0);
        drive.setDecelerationRate(8.0);


        telemetry.addLine("Launcher Velocity Test Ready");
        telemetry.addLine("Gamepad 2: A toggles; D-pad changes RPM");
        telemetry.update();
    }

    @Override
    //////////////////////// TeleOp Loop /////////////////////////////////
    public void loop() {
        // loop() runs repeatedly after PLAY; put changing inputs, motor
        // commands, encoder readings, and live telemetry in this section.

    ///////////////// UPDATE ROBOT DIRECTION ///////////////////
        robot.odo.update();

    ////////////////////////    Drive Code - Game Pad 1       //////////////////////////
        // Back = Reset Position
        if (gamepad1.back && !previousResetButton) {
            robot.odo.resetPosAndIMU();
        }
        previousResetButton = gamepad1.back;


        // Read gamepad 1
        double forward = -gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        // Slow mode while holding right trigger
        if (gamepad1.right_trigger > 0) {
            drive.setMaxSpeed(0.10);
        } else {
            drive.setMaxSpeed(1.0);
        }

        // Left bumper switches to robot-relative
        if (gamepad1.left_bumper) {
            drive.driveRobotRelative(forward, right, rotate);
        } else {
            drive.driveFieldRelative(forward, right, rotate);
        }


    //////////////////////// Launcher Controls - Gamepad 2 ////////////////////////
        // The second condition ensures one change per press, not per loop.
        if (gamepad2.dpad_up && !previousUp) {
            targetRPM = Math.min(MAX_RPM, targetRPM + RPM_STEP);
        }
        if (gamepad2.dpad_down && !previousDown) {
            targetRPM = Math.max(0, targetRPM - RPM_STEP);
        }
        if (gamepad2.a && !previousA) {
            launcherOn = !launcherOn;
        }
        // Update AFTER checking; otherwise a new A press cannot be detected.
        previousUp = gamepad2.dpad_up;
        previousDown = gamepad2.dpad_down;
        previousA = gamepad2.a;

        //Velocity Command
            // FTC setVelocity uses encoder ticks/second, not RPM. - This converts Ticks per second to RMP
            double targetTPS = targetRPM * RobotHardware.LAUNCHER_TICKS_PER_REV / 60.0;
            double commandedRPM = launcherOn ? targetRPM : 0.0;
            robot.launcher.setVelocity(launcherOn ? targetTPS : 0.0);

        // Encoder Feedback ///////////////////////////
            // Read each loop so we can compare actual and requested speeds.
            double actualTPS = robot.launcher.getVelocity();
            double actualRPM = actualTPS * 60.0 / RobotHardware.LAUNCHER_TICKS_PER_REV;
            double errorRPM = commandedRPM - actualRPM;
            // 'Ready' is a speed check, not confirmation a ball is present.
            boolean ready = launcherOn && commandedRPM > 0
                    && Math.abs(errorRPM) <= commandedRPM * READY_TOLERANCE;

    //////////////////////// TELEMETRY /////////////////////////////////
        telemetry.addLine("=== LAUNCHER ===");
        telemetry.addData("Motor", launcherOn ? "ON" : "OFF");
        telemetry.addData("Selected RPM", "%.0f", targetRPM);
        telemetry.addData("Commanded RPM", "%.0f", commandedRPM);
        telemetry.addData("Actual RPM", "%.0f", actualRPM);
        telemetry.addData("Velocity Error RPM", "%.0f", errorRPM);
        telemetry.addData("Target TPS", "%.1f", launcherOn ? targetTPS : 0.0);
        telemetry.addData("Actual TPS", "%.1f", actualTPS);
        telemetry.addData("Ready to Fire", ready ? "YES" : "NO");
        telemetry.addLine("Gamepad 2: A = On/Off, D-pad Up/Down = +/-100 RPM");

        telemetry.addLine("");

        telemetry.addLine("Press Back to reset Yaw");
        telemetry.addLine("Hold Left Bumper for Robot Centric");
        telemetry.addLine("Hold Right Trigger for Slow Mode");
        telemetry.addData("Heading",robot.odo.getHeading(AngleUnit.RADIANS));

        telemetry.update();

    } // Closes TeleOp Loop

    ////////////////////////        STOP WHEN OP MODE ENDS ///////////////////////////////
    @Override
    public void stop() {

        robot.launcher.setVelocity(0);
    }

    //////////////////////// ENTER FUNCTIONS HERE ////////////////////////
} // Closes OpMode
