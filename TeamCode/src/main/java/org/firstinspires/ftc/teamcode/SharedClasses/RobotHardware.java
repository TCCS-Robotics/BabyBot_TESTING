package org.firstinspires.ftc.teamcode.SharedClasses;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class RobotHardware {
    //DcMotorEx has extended capabilities like velocity control

    //Drive Motors     Format  - public DcMotor name; or public DcMotorEx name;
    public DcMotor frontLeftDrive;
    public DcMotor frontRightDrive;
    public DcMotor backLeftDrive;
    public DcMotor backRightDrive;

    //Mechanism Motors
    public DcMotorEx launcher;


    //Servos -  Format  - public Servo name; - or public CRServo name; -


    //Sensors   FORMAT public Sensor Type name;
    public GoBildaPinpointDriver odo;  //Pinpoint for Field Centric
    public Limelight3A limelight;  //Camera for April Tag Detection



    ///////////////////     STATIC VARIABLES     ///////////////////////
    // goBILDA 1:1 motor Encoder Resolution at Output Shaft
    public static final double LAUNCHER_TICKS_PER_REV = 28.0;


    //////////////////////////    INITIALIZE ALL HARDWARE    /////////////////////////////
    public void init(HardwareMap hardwareMap) {

   //////////////////    HARDWARE MAP    ///////////////////////////////////////
        frontLeftDrive = hardwareMap.get(DcMotor.class, "motorFL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "motorFR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "motorBL");
        backRightDrive = hardwareMap.get(DcMotor.class, "motorBR");

        // Mechanism Motors
        launcher = hardwareMap.get(DcMotorEx.class,"launcher");

        // Servos


        // Sensors
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");


    //////////////////    DRIVE MOTOR CONFIGURATION    ///////////////////////////////////////
       //Motor Directions
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

       //Set Brake Behavior
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);






    ////////////////////       Servo Configurations        /////////////////////


    ///////////////// Launcher Configuration/////////////////////////////
        //Set Motor Direction
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);

        //Set Brake Behavior - This allows the flywheel to gradually slow down instead of immediately breaking
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Encoder mode lets the motor controller regulate requested velocity.
        /////  NEEDED TO USE VELOCITY FEEDBACK /////
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Sets the Launcher Starting Velocity to 0 so it starts stopped.
        launcher.setVelocity(0);






    ////////////////////       PINPOINT CONFIGURATION        /////////////////////
        odo.resetPosAndIMU();

    }//Closes Initialization


}// Closes Public Class
