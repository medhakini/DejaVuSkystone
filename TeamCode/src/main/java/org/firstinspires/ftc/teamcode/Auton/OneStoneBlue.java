package org.firstinspires.ftc.teamcode.Auton;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.path.heading.ConstantInterpolator;
import com.acmerobotics.roadrunner.path.heading.LinearInterpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Assemblies.RRMergedDrivetrain;
import org.firstinspires.ftc.teamcode.Assemblies.Sensors;
import org.firstinspires.ftc.teamcode.Assemblies.StoneScorer;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREV;
import org.firstinspires.ftc.teamcode.drive.mecanum.SampleMecanumDriveREVOptimized;
import org.yaml.snakeyaml.scanner.Constant;

import java.util.Vector;

/*
 * This is an example of a more complex path to really test the tuning.
 */

// CASE A: Next to wall
//PLEASE CONVERT TO RADIANS!!!!!!!!!
@Config
@Autonomous(group = "drive")
public class OneStoneBlue extends LinearOpMode {
    StoneScorer ss = new StoneScorer(this);
    Sensors s = new Sensors(this);
    Sensors.SkyStoneLocation skyStoneLocation;

    public static double standardHeading = 0;
    public static double startingX = 0, startingY = 0;
    public static double skystoneLeftX = 40, skystoneCenterX = 40, skystoneRightX = 47.5;
    public static double skystoneLeftY = -12, skystoneCenterY = 8, skystoneRightY = 1;
    public static double distanceForwardToPickUpStone = 17.5;
    public static double distanceForwardToPickUpStoneRight = 7;
    public static double distanceForwardToPickUpStoneCenter = 6;
    public static double distanceForwardToPickUpStoneLeft = 10;
    public static double pulloutX = -30, pulloutY = 35, pulloutHeading = -90;
    public static double centerAngle = -45;
    public static double leftAngle = 45;
    public static double caseRightAngle = -90;
    public static double distanceStrafeLeftForFoundationSide = 55;
    public static double headingForStoneDrop = 90;
    //public static double distanceBackToPark = 25;

    public static double rotationBias = 14;

    public static double foundationRightX = 8;
    public static double foundationRightY = 28;
    public static double foundationHeading = 100;


    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDriveREVOptimized d = new SampleMecanumDriveREVOptimized(hardwareMap);

        ss.init(hardwareMap);
        s.init(hardwareMap);

        waitForStart();

        //TODO reimpl.
        skyStoneLocation = s.findSkystoneBlue();
        //skyStoneLocation = Sensors.SkyStoneLocation.RIGHT;

        if (isStopRequested()) return;

        //starting at -35, 60
        d.setPoseEstimate(new Pose2d(startingX, startingY, standardHeading));

        switch(skyStoneLocation) {
            case LEFT:
                s.shutdown();
                d.followTrajectorySync(
                        d.trajectoryBuilder().lineTo(new Vector2d(skystoneLeftX, strafeConvert(skystoneLeftY)),
                                new LinearInterpolator(Math.toRadians(standardHeading), Math.toRadians(70)))
                                .build());

//                ss.extakeIn();
//                ss.intake(-0.75);

                // go forward to pick up left stone
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .forward(distanceForwardToPickUpStoneLeft)
                                .build()
                );

//                ss.intake(0);
//                ss.clampStone();

                // rotate to straighten out robot
                d.turnSync(Math.toRadians(20 + 3));//27.5

                // strafe to the left to avoid bridge
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .strafeLeft(strafeConvert(14.5))//12
                                .build()
                );

                // travel forward to foundation
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .forward(72.5)
                                .build()
                );

                // rotate 90 to face foundation
                d.turnSync(Math.toRadians(90));

                // back up against foundation
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(7.5)
                                .build()
                );

                // hook foundation
                ss.hookFoundation();
                sleep(500);

                d.setPoseEstimate(new Pose2d(0, 0, 0));

                // spline to turn foundation
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .lineTo(new Vector2d(foundationRightX, foundationRightY), new LinearInterpolator(0, Math.toRadians(foundationHeading + rotationBias)))
                                .build()
                );

                // unhook foundation
                ss.unhookFoundation();

                // push foundation back into wall
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(24)
                                .build()
                );

               ss.extakeOutPartial();
                sleep(500);
                ss.dropStone();
                sleep(500);
                ss.extakeIn();
                sleep(1000);

                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .forward(45)
                                .build()
                );

                break;

            case CENTER:
                s.shutdown();

                d.followTrajectorySync(
                        d.trajectoryBuilder().lineTo(new Vector2d(skystoneCenterX, strafeConvert(skystoneCenterY)),
                                new LinearInterpolator(Math.toRadians(standardHeading), Math.toRadians(centerAngle-20)))
                                .build());

                // move forward to intake block
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .forward(distanceForwardToPickUpStoneCenter)
                                .build()
                );

                ss.intake(0);
                ss.clampStone();

                // strafe right to avoid bridge
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .strafeRight(21)
                                .build()
                );

                d.setPoseEstimate(new Pose2d(0, 0, 0));

                // turn 30 degrees to straighten out the robot
                d.turnSync(Math.toRadians(-27));

                // back up to the foundation
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(78.5)
                                .build()
                );

                // turn 90 degrees to face foundation
                d.turnSync(Math.toRadians(-90));

                // back up to prepare to hook foundation
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(6.5)
                                .build()
                );

                // hook foundation
                ss.hookFoundation();

                sleep(500);

                d.setPoseEstimate(new Pose2d(0, 0, 0));

                // spline to turn foundation
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .lineTo(new Vector2d(foundationRightX, foundationRightY), new LinearInterpolator(0, Math.toRadians(foundationHeading + rotationBias)))
                                .build()
                );

                // unhook foundation
                ss.unhookFoundation();

                // push foundation into the wall
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(24)
                                .build()
                );

                ss.extakeOutPartial();
                sleep(500);
                ss.dropStone();
                sleep(500);
                ss.extakeIn();
                sleep(1000);

                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .forward(45)
                                .build()
                );

                break;

            case RIGHT:
                s.shutdown();

                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .lineTo(new Vector2d(skystoneRightX, strafeConvert(skystoneRightY)),
                                        new LinearInterpolator(Math.toRadians(standardHeading), Math.toRadians(caseRightAngle)))
                                .build());

//                ss.extakeIn();
//
//                ss.intake(-0.75);


                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .forward(distanceForwardToPickUpStoneRight)
                                .build()
                );

                //block picked up
//                ss.intake(0);
//
//                ss.clampStone();

                //moving block to foundation side
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .strafeRight(strafeConvert(16))
                                .build()
                );

                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(79)
                                .build()
                );


                d.turnSync(Math.toRadians(-90));
                //strafe right and cross under bridge
                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(7)
                                .build()
                );

                ss.hookFoundation();

                sleep(500);

                d.setPoseEstimate(new Pose2d(0, 0, 0));

                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .lineTo(new Vector2d(foundationRightX, foundationRightY), new LinearInterpolator(0, Math.toRadians(foundationHeading + rotationBias)))
                                .build()
                );

                ss.unhookFoundation();

                d.followTrajectorySync(
                        d.trajectoryBuilder()
                                .back(24)
                                .build()
                );

                ss.extakeOutPartial();
                sleep(500);
                ss.dropStone();
                sleep(500);
                ss.extakeIn();
                sleep(1000);

                d.followTrajectorySync(
                        d.trajectoryBuilder()
                            .forward(45)
                            .build()
                );

        }
    }

    public static double strafeConvert(double distance) {
        return (1.2 * distance + 3.53);
    }
}

