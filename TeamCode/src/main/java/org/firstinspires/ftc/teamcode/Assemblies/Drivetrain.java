package org.firstinspires.ftc.teamcode.Assemblies;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Drivetrain implements Subassembly {
    DcMotor mtrFL, mtrFR, mtrBL, mtrBR;
    LinearOpMode caller;
    Telemetry telemetry;
    int driveMtrTarget = 1;
    //DT Specs:
    public static final double HD_COUNTS_PER_REV = 560;
    //in inches
    public static final double WHEEL_DIAM = 4;
    public static final double DRIVE_GEAR_RATIO = 1;
    public static final double HD_COUNTS_PER_INCH =
            getCountsPerInch(DRIVE_GEAR_RATIO, HD_COUNTS_PER_REV, WHEEL_DIAM);
    private boolean ccwRotation = false;

    //for now, I'll instantiate two more motors to act like the odometry wheels
    private DcMotor odVert, odHoriz;


    public Drivetrain(LinearOpMode caller) {
        this.caller = caller;
        telemetry = caller.telemetry;
    }

    @Override
    public void init() {
        mtrFL = caller.hardwareMap.get(DcMotor.class, ConfigurationData.DRIVETRAIN_MOTOR_NAMES[0]);
        mtrFR = caller.hardwareMap.get(DcMotor.class, ConfigurationData.DRIVETRAIN_MOTOR_NAMES[1]);
        mtrBL = caller.hardwareMap.get(DcMotor.class, ConfigurationData.DRIVETRAIN_MOTOR_NAMES[2]);
        mtrBR = caller.hardwareMap.get(DcMotor.class, ConfigurationData.DRIVETRAIN_MOTOR_NAMES[3]);

        mtrFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mtrFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mtrBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mtrBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        mtrFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mtrFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mtrBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        mtrBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //init'ng the odom
        odVert = caller.hardwareMap.get(DcMotor.class, "od_vert");
        odVert.setDirection(DcMotorSimple.Direction.FORWARD);
        odHoriz = caller.hardwareMap.get(DcMotor.class, "od_horiz");
        odHoriz.setDirection(DcMotorSimple.Direction.FORWARD);

        mtrFL.setDirection(DcMotorSimple.Direction.REVERSE);
        mtrFR.setDirection(DcMotorSimple.Direction.FORWARD);
        mtrBL.setDirection(DcMotorSimple.Direction.REVERSE);
        mtrBR.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    @Override
    public void status() {

    }

    public ArrayList<Double> getCurrentEncoderValues() {
        ArrayList<Double> currentEncoderValues = new ArrayList<>();
        currentEncoderValues.add(((double) mtrFL.getCurrentPosition()));
        currentEncoderValues.add(((double) mtrFR.getCurrentPosition()));
        return currentEncoderValues;
    }



    //@Override
    public void run() {

    }

    public enum Direction {
        FWD,BACK,LEFT,RIGHT
    }

    private static double getCountsPerInch(double gearRatio, double countsRevolution, double diam) {
        return (gearRatio * countsRevolution / (Math.PI * diam));
    }

    public void translate(Direction dir, double inches, double speed) {
        driveMtrTarget = (int) (Math.abs(inches) * HD_COUNTS_PER_INCH);

        int a, b;
        for (int i = 0; i<4 && !caller.isStopRequested(); i++) {
            switch (dir) {
                case LEFT:
                    a = -1;
                    b = 1;
                    break;
                case RIGHT:
                    a = 1;
                    b = -1;
                    break;
                case FWD:
                    a = 1;
                    b = 1;
                    break;
                case BACK:
                    a = -1;
                    b = -1;
                    break;
                default:
                    telemetry.addData("Err", "Unknown dir %s", dir.toString());
                    telemetry.update();
                    a=0;
                    b=0;
                    break;
            }

            mtrFL.setTargetPosition(a * driveMtrTarget);
            mtrFR.setTargetPosition(b * driveMtrTarget);
            mtrBL.setTargetPosition(b * driveMtrTarget);
            mtrBR.setTargetPosition(a * driveMtrTarget);

            mtrFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            mtrFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            mtrBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            mtrBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            mtrFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            mtrFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            mtrBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            mtrBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        if(!caller.isStopRequested()){
            mtrFL.setPower(speed);
            mtrFR.setPower(speed);
            mtrBL.setPower(speed);
            mtrBR.setPower(speed);
        }

        while(!caller.isStopRequested() &&
                ((mtrFL.isBusy()) && (mtrFR.isBusy()) && (mtrBL.isBusy()) && (mtrBR.isBusy())) ) {
            //TODO change telemetry name to enum
            telemetry.addData("0mtrFl", "%7d : %7d",
                    mtrFL.getCurrentPosition(), driveMtrTarget);
            telemetry.addData("1mtrFR", "%7d : %7d",
                    mtrFR.getCurrentPosition(), driveMtrTarget);
            telemetry.addData("2mtrBR", "%7d : %7d",
                    mtrBL.getCurrentPosition(), driveMtrTarget);
            telemetry.addData("3mtrBL", "%7d : %7d",
                    mtrBR.getCurrentPosition(), driveMtrTarget);

            telemetry.update();
        }

        if(!caller.isStopRequested()){
            mtrFL.setPower(0);
            mtrFR.setPower(0);
            mtrBL.setPower(0);
            mtrBR.setPower(0);
        }

        if(!caller.isStopRequested()) {
            mtrFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mtrFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mtrBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mtrBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

    }

}

