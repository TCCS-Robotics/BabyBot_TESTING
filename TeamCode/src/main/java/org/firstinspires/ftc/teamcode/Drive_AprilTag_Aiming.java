
package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeRadians;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "April Tag Aiming8", group = "Robot")
//@Disabled
public class Drive_AprilTag_Aiming extends OpMode {

    //Declare Hardware

    // Motors - format DcMotor What_You_Name_It
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;


    //Servos - Format Servo What_You_Name_It
    // or CRServo What_You_Name_It (Continuous Rotation Servos)


    //Sensors
    GoBildaPinpointDriver odo;
    Limelight3A limelight;


    ////////////////// APRILTAG SETTINGS //////////////////

    //AprilTag ID to automatically rotate toward
    private static final int TARGET_TAG_ID = 7;

    //Limelight AprilTag pipeline number
    private static final int APRILTAG_PIPELINE = 0;

    //Proportional rotation constant
    private static final double KP = 0.015;

    //Maximum automatic rotation power
    private static final double MAX_TURN_POWER = 0.30;

    //Acceptable horizontal error in degrees
    private static final double TOLERANCE = 1.5;

    //Maximum age of Limelight result in milliseconds
    private static final double MAX_STALENESS = 100;


    @Override
    //////////////////////// Initialization //////////////////////////////
    public void init() {

        /////////////////////////////Hardware Map/////////////////////////

        //Motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "motorFL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "motorFR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "motorBL");
        backRightDrive = hardwareMap.get(DcMotor.class, "motorBR");


        //Servos


        //Sensors
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");

        limelight = hardwareMap.get(Limelight3A.class, "limelight");


        /*Set Motor Directions so all wheels rotate forward
          with positive power values.
          This will be DIFFERENT on EACH ROBOT depending
          on wheel and motor placement*/

        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);


        //Set motors to brake when power is zero
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Reset the Pinpoint position
        odo.resetPosAndIMU();


        /////////////// LIMELIGHT INITIALIZATION ///////////////

        //Set Limelight polling rate
        limelight.setPollRateHz(50);

        //Select AprilTag detection pipeline
        limelight.pipelineSwitch(APRILTAG_PIPELINE);

        //Start Limelight
        limelight.start();


        telemetry.addLine("Robot Initialized");
        telemetry.addLine("Hold B for AprilTag Alignment");
        telemetry.update();
    }


    @Override
    ////////////////// TeleOp Loop ///////////////////////////////////
    public void loop() {

        telemetry.addLine("Press A to reset Yaw");
        telemetry.addLine("Hold Left Bumper for Robot Centric");
        telemetry.addLine("Hold Right Trigger for Slow Mode");
        telemetry.addLine("Left joystick sets robot direction");
        telemetry.addLine("Right joystick turns the robot");
        telemetry.addLine("Hold B to align with AprilTag");


        //Update Pinpoint Telemetry
        odo.update();

        telemetry.addData("Heading",
                odo.getHeading(AngleUnit.RADIANS));


        ////////////////   LAUNCHER   //////////////



        //////////////////////// Drive Code //////////////////////////

        /*
            Left Bumper = Robot Centric
            Right Trigger = Slow Mode
            A Button = Reset Yaw
            B Button = AprilTag Automatic Rotation
        */


        //Reset the Yaw with the A Button
        if (gamepad1.a) {
            odo.resetPosAndIMU();
        }


        //Get joystick values
        double forward = -gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;


        /////////////// APRILTAG AUTOMATIC ROTATION ///////////////

        if (gamepad1.b) {

            //Get automatic rotation value from Limelight
            rotate = getAprilTagRotation();

        }


        /////////////// SLOW MODE ///////////////

        if (gamepad1.right_trigger > 0) {

            forward *= 0.10;
            right *= 0.10;
            rotate *= 0.10;
        }


        /////////////// ROBOT OR FIELD RELATIVE DRIVE ///////////////

        if (gamepad1.left_bumper) {

            //Robot Centric
            drive(forward, right, rotate);

        } else {

            //Field Centric
            driveFieldRelative(forward, right, rotate);
        }


        telemetry.update();

    }//Closes TeleOp Loop


    @Override
    public void stop() {

        //Stop drivetrain motors
        drive(0, 0, 0);

        //Stop Limelight
        limelight.stop();
    }


    ////////////////// ENTER FUNCTIONS HERE ////////////////////////////


    /////////////// APRILTAG ROTATION FUNCTION ///////////////

    //Calculates how much the robot should rotate
    //to center the desired AprilTag in the camera view.

    private double getAprilTagRotation() {

        //Get latest Limelight result
        LLResult result = limelight.getLatestResult();

        boolean tagFound = false;
        double tx = 0;


        //Check that Limelight result is valid and recent
        if (result != null && result.isValid()
                && result.getStaleness() < MAX_STALENESS) {

            //Search for target AprilTag
            for (LLResultTypes.FiducialResult tag :
                    result.getFiducialResults()) {

                if (tag.getFiducialId() == TARGET_TAG_ID) {

                    tx = tag.getTargetXDegrees();

                    tagFound = true;

                    break;
                }
            }
        }


        if (tagFound) {

            //Calculate rotation using proportional control
            double turnPower = calculateTurnPower(tx);

            //Display AprilTag telemetry
            telemetry.addData("AprilTag ID", TARGET_TAG_ID);

            telemetry.addData("AprilTag tx",
                    "%.2f degrees", tx);

            telemetry.addData("Auto Rotation Power",
                    "%.2f", turnPower);


            if (Math.abs(tx) <= TOLERANCE) {

                telemetry.addLine("APRILTAG CENTERED");

            } else {

                telemetry.addLine("ALIGNING WITH APRILTAG");
            }

            return turnPower;

        } else {

            telemetry.addLine("TARGET APRILTAG NOT FOUND");

            //Stop automatic rotation when tag is lost
            return 0;
        }
    }


    /////////////// PROPORTIONAL CONTROL FUNCTION ///////////////

    private double calculateTurnPower(double tx) {

        double turnPower = 0;

        //Only rotate when outside alignment tolerance
        if (Math.abs(tx) > TOLERANCE) {

            //Calculate proportional rotation
            turnPower = KP * tx;

            //Limit maximum rotation power
            turnPower = Math.max(
                    -MAX_TURN_POWER,
                    Math.min(MAX_TURN_POWER, turnPower));
        }

        return turnPower;
    }


    /////////////// FIELD RELATIVE DRIVE FUNCTION ///////////////

    //This routine drives the robot field relative
    private void driveFieldRelative(
            double forward, double right, double rotate) {

        //First, convert direction being asked
        //to drive to polar coordinates
        double theta = Math.atan2(forward, right);
        double r = Math.hypot(right, forward);

        //Get heading from Pinpoint
        double heading = odo.getHeading(AngleUnit.RADIANS);

        //Rotate joystick vector by robot heading
        theta = normalizeRadians(theta - heading);

        //Convert back to Cartesian
        double newForward = r * Math.sin(theta);
        double newRight = r * Math.cos(theta);

        //Call robot-relative drive function
        drive(newForward, newRight, rotate);
    }


    /////////////// ROBOT RELATIVE DRIVE FUNCTION ///////////////

    public void drive(double forward, double right, double rotate) {

        //Calculate mecanum wheel powers
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backRightPower = forward + right - rotate;
        double backLeftPower = forward - right + rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;


        //Normalize motor powers
        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));


        //Apply motor powers
        frontLeftDrive.setPower(
                maxSpeed * (frontLeftPower / maxPower));

        frontRightDrive.setPower(
                maxSpeed * (frontRightPower / maxPower));

        backLeftDrive.setPower(
                maxSpeed * (backLeftPower / maxPower));

        backRightDrive.setPower(
                maxSpeed * (backRightPower / maxPower));
    }

}//Closes OpMode
