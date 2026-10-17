package org.firstinspires.ftc.teamcode;

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
    public DcMotorEx flywheel;


    //Servos -  Format  - public Servo name; - or public CRServo name; -


    //Sensors   FORMAT public Sensor Type name;
    public GoBildaPinpointDriver odo;  //Pinpoint for Field Centric
    public Limelight3A limelight;  //Camera for April Tag Detection


    //Static Variables


    //////////////////////////    INITIALIZE ALL HARDWARE    /////////////////////////////
    public void init(HardwareMap hardwareMap) {

        // Drive Motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "motorFL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "motorFR");
        backLeftDrive = hardwareMap.get(DcMotor.class, "motorBL");
        backRightDrive = hardwareMap.get(DcMotor.class, "motorBR");

        // Mechanism Motors
        flywheel = hardwareMap.get(DcMotorEx.class,"flywheel");

        // Servos


        // Sensors
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Motor Directions
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        //Mechanism Motor Directions
        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);

        // Brake Mode
        frontLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Servo Directions


        // Launcher Configuration




        // Initialize Servos


        // Reset Odometry
        odo.resetPosAndIMU();

    }//Closes Initialization


}// Closes Public Class
