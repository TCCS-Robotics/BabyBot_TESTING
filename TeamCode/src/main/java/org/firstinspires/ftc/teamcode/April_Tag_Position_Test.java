package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.normalizeRadians;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import java.util.List;

@TeleOp(name = "Limelight April Tag Detection", group = "Robot")
//@Disabled
public class April_Tag_Position_Test extends OpMode {

    //Declare Hardware

    // Motors - format DcMotor What_You_Name_It


    //Servos - Format Servo What_You_Name_It
    // or CRServo What_You_Name_It


    //Sensors
    private Limelight3A limelight;

    //AprilTag pipeline number
    private static final int APRILTAG_PIPELINE = 0;


    @Override
    //////////////////////// Initialization //////////////////////////////
    public void init() {

        /////////////////////////////Hardware Map/////////////////////////

        //Motors


        //Servos


        //Sensors
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        //Set the Limelight polling rate
        limelight.setPollRateHz(50);

        //Select AprilTag pipeline
        limelight.pipelineSwitch(APRILTAG_PIPELINE);

        //Start the Limelight
        limelight.start();


        /*Set Motor Directions so all wheels rotate forward
          with positive power values.
          This will be DIFFERENT on EACH ROBOT depending
          on wheel and motor placement*/


        // Reset the Pinpoint position


        telemetry.addLine("Limelight Initialized");
        telemetry.addLine("Ready to Detect AprilTags");
        telemetry.update();
    }// Closes Initialization


    @Override
    ////////////////// TeleOp Loop ///////////////////////////////////
    public void loop() {

        ////////////////// APRIL TAG DETECTION CODE ///////////////////////////////////
        //Get the latest result from Limelight
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {

            //Get list of detected AprilTags
            List<LLResultTypes.FiducialResult> tags =
                    result.getFiducialResults();

            telemetry.addData("Number of Tags", tags.size());

            //Loop through all detected AprilTags
            for (LLResultTypes.FiducialResult tag : tags) {

                //Get AprilTag ID
                int tagID = tag.getFiducialId();

                //Get horizontal and vertical angles
                double tx = tag.getTargetXDegrees();
                double ty = tag.getTargetYDegrees();

                telemetry.addLine("-------------------------");
                telemetry.addData("AprilTag ID", tagID);

                telemetry.addData(
                        "Horizontal Angle (tx)", "%.2f deg", tx);

                telemetry.addData(
                        "Vertical Angle (ty)", "%.2f deg", ty);


                //Determine whether tag is left or right
                if (tx > 1.0) {
                    telemetry.addLine("Tag is RIGHT of camera center");
                }
                else if (tx < -1.0) {
                    telemetry.addLine("Tag is LEFT of camera center");
                }
                else {
                    telemetry.addLine("Tag is CENTERED");
                }


                //Get AprilTag position relative to camera
                Pose3D tagPose = tag.getTargetPoseCameraSpace();

                if (tagPose != null) {

                    double x = tagPose.getPosition()
                            .toUnit(DistanceUnit.INCH).x;

                    double y = tagPose.getPosition()
                            .toUnit(DistanceUnit.INCH).y;

                    double z = tagPose.getPosition()
                            .toUnit(DistanceUnit.INCH).z;


                    telemetry.addData(
                            "X Position (in)", "%.2f", x);

                    telemetry.addData(
                            "Y Position (in)", "%.2f", y);

                    telemetry.addData(
                            "Z Position (in)", "%.2f", z);
                }
                else {
                    telemetry.addLine("3D Position Unavailable");
                }
            }

        }
        else {
            telemetry.addLine("No AprilTags Detected");
        }
    //////////////////      END OF APRIL TAG DETECTION CODE     ///////////////////////////////////
        telemetry.update();


    }// Closes TeleOp Loop


    @Override
    public void stop() {

        //Stop Limelight when OpMode ends
        limelight.stop();
    }


    ////////////////// ENTER FUNCTIONS HERE ////////////////////////////


}//Closes OpMode