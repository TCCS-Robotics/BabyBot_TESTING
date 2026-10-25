package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.SharedClasses.MecanumAutoDrive;
import org.firstinspires.ftc.teamcode.SharedClasses.RobotHardware;

@Autonomous(name = "JH Simple Encoder Auto", group = "JH")
public class SimpleAuto extends LinearOpMode {
    private final RobotHardware robot = new RobotHardware();

    @Override
    public void runOpMode() {
    ///////////////////////////////////   Initialization Code Goes Here   /////////////////////////////
        robot.init(hardwareMap);   //Hardware map
        MecanumAutoDrive autoDrive = new MecanumAutoDrive(robot, this);//Mecanum Auto Class

        //Telemetry
        telemetry.addLine("Ready. Place robot in a clear test area.");
        telemetry.update();

        waitForStart();
    ///////////////////////////////////   AUTO CODE GOES HERE   /////////////////////////////
        if (!opModeIsActive()) return;

        // Stop the sequence if a movement times out or Stop is pressed. (parameters: distance/degree, power, timeout)

        if (!autoDrive.move(MecanumAutoDrive.Direction.FORWARD, 24, 0.5, 6.0)) return; //Drive Forward 24 inches @ 50% power
        if (!autoDrive.move(MecanumAutoDrive.Direction.RIGHT, 12, 0.4, 6.0)) return; //Drive Right 12 inches @ 40% power
        if (!autoDrive.move(MecanumAutoDrive.Direction.ROTATE_LEFT, 90, 0.3, 6.0)) return; //Rotate Left 90 Degrees @ 30% power
        if (!autoDrive.move(MecanumAutoDrive.Direction.BACKWARD, 10, 0.5, 6.0)) return; //Drive Backward 10 inches @ 50% power


    ///////////////////////////////////   Telemetry   /////////////////////////////
        telemetry.addLine("Autonomous complete!");
        telemetry.update();
    }//Closes Run OpMode
    //////////////////////   FUNCTIONS GO HERE   /////////////////////////////
}//Closes Linear OpMode
