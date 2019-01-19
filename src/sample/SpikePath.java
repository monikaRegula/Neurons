package sample;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;

import java.util.ArrayList;
import java.util.Arrays;

public class SpikePath implements StepHandler {

    //atrubuty klasy
    private ArrayList<Double> uValues = new ArrayList<>();
    private ArrayList<Double> vValues = new ArrayList<>();
    private ArrayList<Double> times = new ArrayList<>();
    private double time = 0;


    public double getTime() { return time; }

    public ArrayList<Double> getuValues() { return uValues; }

    public ArrayList<Double> getvValues() { return vValues; }

    public ArrayList<Double> getTimes() { return times; }


    //tu sie nic nie dzieje
    @Override
    public void init(double t, double[] u, double dudt) {
    }

    // zapisuje kroki całkowania w liście tablicowej
    @Override
    public void handleStep(StepInterpolator stepInterpolator, boolean b) throws MaxCountExceededException {

        //pobierma czas obecny
        double t = stepInterpolator.getCurrentTime();
        //dla czasu obecnego pobieram stan
        double [] u = stepInterpolator.getInterpolatedState();

        time = t;
        //dodaje do list tablicowych wartości:
        uValues.add(u[0]);
        vValues.add(u[1]);
        times.add(time);

        System.out.println("t= " + t + " " + Arrays.toString(u));
    }
}
