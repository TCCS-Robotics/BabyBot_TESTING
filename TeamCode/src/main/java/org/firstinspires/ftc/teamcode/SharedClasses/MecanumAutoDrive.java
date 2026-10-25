package org.firstinspires.ftc.teamcode.SharedClasses;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/** Simple blocking encoder drive for a junior-high FTC mecanum robot. */
public class MecanumAutoDrive {
    public enum Direction { FORWARD, BACKWARD, LEFT, RIGHT, ROTATE_LEFT, ROTATE_RIGHT }

    // 312 RPM goBILDA Yellow Jacket (19.2:1) nominal output-shaft counts.
    // Verify your exact motor/gearbox encoder specification.
    public static double TICKS_PER_REV = 537.7;
    public static double WHEEL_DIAMETER_INCHES = 96.0 / 25.4;
    public static double STRAFE_FACTOR = 1.0; // Tune after testing.
    public static double TURN_TICKS_PER_DEGREE = 10.0; // PLACEHOLDER: calibrate on robot.
    public static double DEFAULT_TIMEOUT_SECONDS = 5.0;
    private static final int POSITION_TOLERANCE_TICKS = 15;

    private final RobotHardware robot;
    private final LinearOpMode opMode;
    private final DcMotor[] motors;

    public MecanumAutoDrive(RobotHardware robot, LinearOpMode opMode) {
        this.robot = robot;
        this.opMode = opMode;
        motors = new DcMotor[] {
                robot.frontLeftDrive, robot.frontRightDrive,
                robot.backLeftDrive, robot.backRightDrive
        };
    }

    public void forward(double inches, double power) { move(Direction.FORWARD, inches, power); }
    public void backward(double inches, double power) { move(Direction.BACKWARD, inches, power); }
    public void left(double inches, double power) { move(Direction.LEFT, inches, power); }
    public void right(double inches, double power) { move(Direction.RIGHT, inches, power); }
    public void rotateLeft(double degrees, double power) { move(Direction.ROTATE_LEFT, degrees, power); }
    public void rotateRight(double degrees, double power) { move(Direction.ROTATE_RIGHT, degrees, power); }

    /** Distance is inches for translation, degrees for rotation. Returns true on success. */
    public boolean move(Direction direction, double amount, double power) {
        return move(direction, amount, power, DEFAULT_TIMEOUT_SECONDS);
    }

    public boolean move(Direction direction, double amount, double power, double timeoutSeconds) {
        if (!opMode.opModeIsActive() || amount < 0 || power <= 0 || power > 1 || timeoutSeconds <= 0) {
            stop();
            return false;
        }
        if (amount == 0) { stop(); return true; }

        final boolean turning = direction == Direction.ROTATE_LEFT || direction == Direction.ROTATE_RIGHT;
        double ticks = turning ? amount * TURN_TICKS_PER_DEGREE
                : amount * (TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_INCHES))
                * ((direction == Direction.LEFT || direction == Direction.RIGHT) ? STRAFE_FACTOR : 1.0);
        int step = (int) Math.round(ticks);
        if (step == 0) { stop(); return true; }

        // Order: front left, front right, back left, back right.
        int[] signs;
        switch (direction) {
            case BACKWARD:     signs = new int[] {-1, -1, -1, -1}; break;
            case RIGHT:        signs = new int[] { 1, -1, -1,  1}; break;
            case LEFT:         signs = new int[] {-1,  1,  1, -1}; break;
            case ROTATE_RIGHT: signs = new int[] { 1, -1,  1, -1}; break;
            case ROTATE_LEFT:  signs = new int[] {-1,  1, -1,  1}; break;
            case FORWARD:
            default:           signs = new int[] { 1,  1,  1,  1}; break;
        }

        // Targets are relative to the current encoder counts. No reset between moves.
        for (int i = 0; i < motors.length; i++) {
            motors[i].setTargetPosition(motors[i].getCurrentPosition() + signs[i] * step);
            motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motors[i].setPower(Math.abs(power));
        }

        ElapsedTime timer = new ElapsedTime();
        boolean completed = false;
        try {
            while (opMode.opModeIsActive() && !opMode.isStopRequested()
                    && timer.seconds() < timeoutSeconds) {
                boolean allAtTarget = true;
                for (DcMotor motor : motors) {
                    if (Math.abs(motor.getTargetPosition() - motor.getCurrentPosition())
                            > POSITION_TOLERANCE_TICKS) {
                        allAtTarget = false;
                    }
                }
                opMode.telemetry.addData("Auto movement", direction);
                opMode.telemetry.addData("Elapsed (s)", "%.1f / %.1f", timer.seconds(), timeoutSeconds);
                opMode.telemetry.addData("FL target/current", "%d / %d", motors[0].getTargetPosition(), motors[0].getCurrentPosition());
                opMode.telemetry.addData("FR target/current", "%d / %d", motors[1].getTargetPosition(), motors[1].getCurrentPosition());
                opMode.telemetry.addData("BL target/current", "%d / %d", motors[2].getTargetPosition(), motors[2].getCurrentPosition());
                opMode.telemetry.addData("BR target/current", "%d / %d", motors[3].getTargetPosition(), motors[3].getCurrentPosition());
                opMode.telemetry.update();
                if (allAtTarget) { completed = true; break; }
                opMode.idle();
            }
        } finally {
            stop();
        }
        opMode.telemetry.addData("Movement result", completed ? "COMPLETE" : "TIMEOUT / STOPPED");
        opMode.telemetry.update();
        return completed;
    }

    public void stop() {
        for (DcMotor motor : motors) {
            motor.setPower(0);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}
