
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Launcher Velocity Test", group = "Test")
public class LauncherVelocityTest extends LinearOpMode {

    private DcMotorEx launcher;

    // CHANGE THIS to your motor's encoder ticks per revolution.
    // Use the motor shaft value, not the output shaft value.
    private static final double TICKS_PER_REV = 28.0;

    private static final double RPM_STEP = 100;
    private static final double MAX_RPM = 6000;

    private double targetRPM = 0;
    private boolean launcherOn = false;

    @Override
    public void runOpMode() {

        // Must match the Control Hub configuration name
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        launcher.setDirection(DcMotor.Direction.FORWARD);
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.FLOAT);

        telemetry.addLine("Launcher Velocity Test Ready");
        telemetry.update();

        waitForStart();

        boolean lastUp = false;
        boolean lastDown = false;
        //Set A button to False
        boolean lastA = false;


        while (opModeIsActive()) {

        /* Use User 2 - D Pad Up and Down to increase & Decrease the Set Velocity */
            // Increase target RPM
            if (gamepad2.dpad_up && !lastUp) {
                targetRPM = Math.min(MAX_RPM, targetRPM + RPM_STEP);
            }

            // Decrease target RPM
            if (gamepad2.dpad_down && !lastDown) {
                targetRPM = Math.max(0, targetRPM - RPM_STEP);
            }

            lastUp = gamepad2.dpad_up;
            lastDown = gamepad2.dpad_down;

            // Game Pad 2 A button Toggles Launcher on and Off
                        //Sets lastA to false and each time you hit the "A" button it toggles between true and False
            //Set last A to fales in init phase and then set lastA to A button in teleop
            lastA = gamepad2.a;
            // if game pad 2 is pushed and & last A is not false set the launcher from On to Not On

            if (gamepad2.a && !lastA) {
                launcherOn = !launcherOn;
            }

            // Convert target RPM to encoder ticks/second
            // Need variables declared before init phase:
                    //private static final double TICKS_PER_REV = 28.0;
                    //private static final double RPM_STEP = 100;
                    //private static final double MAX_RPM = 6000;
                    // private double targetRPM = 0;
                    // private boolean launcherOn = false;
            double targetTPS = targetRPM * TICKS_PER_REV / 60.0;

            // Command motor velocity
            if (launcherOn) {
                launcher.setVelocity(targetTPS);
            }
            else {
                launcher.setVelocity(0);
            }



            // Read actual encoder velocity
            double actualTPS = launcher.getVelocity();

            // Convert measured velocity to RPM
            double actualRPM =
                    actualTPS * 60.0 / TICKS_PER_REV;

            double commandedRPM =
                    launcherOn ? targetRPM : 0;

            // Calculate velocity error
            double errorRPM =
                    commandedRPM - actualRPM;

            // Ready when within 5% of target
            double tolerance =
                    commandedRPM * 0.05;

            boolean ready =
                    launcherOn
                            && commandedRPM > 0
                            && Math.abs(errorRPM) <= tolerance;

            // TELEMETRY
            telemetry.addLine("=== LAUNCHER ===");

            telemetry.addData("Motor", launcherOn ? "ON" : "OFF");
            telemetry.addData("Selected RPM", "%.0f", targetRPM);
            telemetry.addData("Commanded RPM", "%.0f", commandedRPM);
            telemetry.addData("Actual RPM", "%.0f", actualRPM);
            telemetry.addData("Velocity Error", "%.0f", errorRPM);

            telemetry.addData("Target TPS", "%.1f",
                    launcherOn ? targetTPS : 0);
            telemetry.addData("Actual TPS", "%.1f", actualTPS);

            telemetry.addData("Ready to Fire",
                    ready ? "YES" : "NO");

            telemetry.addLine("----------------");
            telemetry.addLine("A: Toggle Launcher");
            telemetry.addLine("DPad Up: +100 RPM");
            telemetry.addLine("DPad Down: -100 RPM");

            telemetry.update();

            sleep(50);
        }

        launcher.setVelocity(0);
    }
}
