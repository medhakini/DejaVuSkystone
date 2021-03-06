package org.firstinspires.ftc.teamcode.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp (name="Hardware Testing", group="Functionality")
@Disabled
public class HardwareTesting extends LinearOpMode {
    DcMotorEx mtrFL, mtrFR, mtrBL, mtrBR;
    DcMotorEx mtrVertical, leftRoller, rightRoller;

    @Override
    public void runOpMode() {
        mtrFL = hardwareMap.get(DcMotorEx.class, "fl");
        mtrFR = hardwareMap.get(DcMotorEx.class, "fr");
        mtrBL = hardwareMap.get(DcMotorEx.class, "bl");
        mtrBR = hardwareMap.get(DcMotorEx.class, "br");
        mtrVertical = hardwareMap.get(DcMotorEx.class, "vertical");

        mtrFL.setDirection(DcMotorSimple.Direction.REVERSE);
        mtrFR.setDirection(DcMotorSimple.Direction.FORWARD);
        mtrBL.setDirection(DcMotorSimple.Direction.REVERSE);
        mtrBR.setDirection(DcMotorSimple.Direction.FORWARD);

        mtrFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        mtrFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        mtrBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        mtrBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        mtrFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mtrFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mtrBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mtrBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftRoller = hardwareMap.get(DcMotorEx.class, "leftRoller");
        rightRoller = hardwareMap.get(DcMotorEx.class, "rightRoller");

        leftRoller.setDirection(DcMotorSimple.Direction.FORWARD);
        rightRoller.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while (opModeIsActive()) {

            mtrVertical.setPower(gamepad1.right_stick_y / 2);

            if (gamepad1.a) {
                leftRoller.setPower(1);
                rightRoller.setPower(1);
            } else if (gamepad1.b){
                leftRoller.setPower(-1);
                rightRoller.setPower(-1);
            } else {
                leftRoller.setPower(0);
                rightRoller.setPower(0);
            }

        }
    }
}
