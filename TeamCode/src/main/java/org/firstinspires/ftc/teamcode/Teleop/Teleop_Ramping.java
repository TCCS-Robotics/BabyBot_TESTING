package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.SharedClasses.MecanumDrive_Smooth;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;


@TeleOp(name = "TeleOp", group = "Robot")
//@Disabled
public class Teleop_Ramping extends OpMode {
    //Import Hardware Class & Mecanum Drive Class
    RobotHardware robot = new RobotHardware();
    MecanumDrive_Smooth drive;

    /////////////////////   Variables   /////////////////////
    private boolean previousResetButton = false;


    @Override
    //////////////////////// Initialization //////////////////////////////
    public void init() {
    //Initialize Hardware and Mecanum Drive Classes
        robot.init(hardwareMap);
        drive = new MecanumDrive_Smooth(robot);
    //Initialize Ramping value and acceleration values
        drive.setRampingEnabled(true); // Set to False if you want to Disable Ramping
        drive.setAccelerationRate(2.0);
        drive.setDecelerationRate(8.0);

   }

    @Override
    /////////////// TeleOp Loop ////////////////////////////////////
    public void loop() {

        // Update Pinpoint
        robot.odo.update();


    //////////////   LAUNCHER   //////////////




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


    ////////////////////////    TELEMETRY        //////////////////////////
        telemetry.addLine("Press Back to reset Yaw");
        telemetry.addLine("Hold Left Bumper for Robot Centric");
        telemetry.addLine("Hold Right Trigger for Slow Mode");
        telemetry.addLine("Left joystick sets robot direction");
        telemetry.addLine("Right joystick turns the robot");
        telemetry.addData("Heading",robot.odo.getHeading(AngleUnit.RADIANS));
        telemetry.update();


    }//Closes TeleOp Loop

////////////////////////////////// ENTER FUNCTIONS HERE//////////////////////////////////////



}//Closes Op Mode
