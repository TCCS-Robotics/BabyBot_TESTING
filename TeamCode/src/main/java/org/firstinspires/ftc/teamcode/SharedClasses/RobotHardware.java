package org.firstinspires.ftc.teamcode.SharedClasses;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;


public class RobotHardware {
    //DcMotorEx has extended capabilities like velocity control

    //Drive Motors     Format  - public DcMotor name; or public DcMotorEx name;
    public DcMotor frontLeftDrive;
    public DcMotor frontRightDrive;
    public DcMotor backLeftDrive;
    public DcMotor backRightDrive;

    //Mechanism Motors
    public DcMotorEx flywheel;
    public DcMotor turret;


    //Servos -  Format  - public Servo name; - or public CRServo name; -


    //Sensors   FORMAT public Sensor Type name;
    public GoBildaPinpointDriver odo;  //Pinpoint for Field Centric
    public Limelight3A limelight;  //Camera for April Tag Detection



    ///////////////////     STATIC VARIABLES     ///////////////////////
    // goBILDA 1:1 motor Encoder Resolution at Output Shaft
    public static final double flywheel_TICKS_PER_REV = 28.0;
    public static final int flywheel_TARGET_VELOCITY = 1250;
    public static final int flywheel_MIN_VELOCITY = 1200;


    //////////////////////////    INITIALIZE ALL HARDWARE    /////////////////////////////
    public void init(HardwareMap hardwareMap) {

   //////////////////    HARDWARE MAP    ///////////////////////////////////////
        frontLeftDrive = hardwareMap.get(DcMotor.class, "motorFL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "motorFR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "motorBL");
        backRightDrive = hardwareMap.get(DcMotor.class, "motorBR");

        // Mechanism Motors
        flywheel = hardwareMap.get(DcMotorEx.class,"flywheel");
        turret = hardwareMap.get(DcMotorEx.class,"turret");

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


    ///////////////// Turret Configuration/////////////////////////////
        // Turret: start stopped, brake when power is released.
        // Encoder position is not reset here; add calibrated limits before full-speed use.
        turret.setDirection(DcMotor.Direction.REVERSE); // NEED TO CHECK THIS MOTOR DIRECTION
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turret.setPower(0);

    ///////////////// Flywheel Configuration/////////////////////////////
        flywheel.setDirection(DcMotor.Direction.FORWARD); // NEED TO CHECK THIS MOTOR DIRECTION

        //Set Brake Behavior - This allows the flywheel to gradually slow down instead of immediately breaking
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Encoder mode lets the motor controller regulate requested velocity.
        /////  NEEDED TO USE VELOCITY FEEDBACK /////
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(40, 0, 0, 12.5));

        // Sets the flywheel Starting Velocity to 0 so it starts stopped.
        flywheel.setVelocity(0);






    ////////////////////       PINPOINT CONFIGURATION        /////////////////////
        odo.resetPosAndIMU();

    }//Closes Initialization


}// Closes Public Class
