package sample;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class SpikeODE implements FirstOrderDifferentialEquations {
//Klasa całkuje równania pochodnych

    //atrybuty klasy

    //a współczynnik opisuje czas powrotu zmiennej u
    private double a;
    //b opisuje czułość zmiennej powrotu u na podprogowe fluktuacje potencjału błonowego v
    private double b;
    //I[mV] to prąd synaptyczny lub podane do komórki prądy stałe
    private double I;

    //konstruktor klasy
    public SpikeODE(double a, double b, double i) {
        this.a = a;
        this.b = b;
        I = i;
    }

    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public void computeDerivatives(double t, double[] u, double[] dudt) throws MaxCountExceededException, DimensionMismatchException {

        /*u[0] to pochodna  du/dt

        u to zmienna odpowiadająca za powrót błony do stanu spoczynkowego związana z aktywnością
        jonów potasu i aktywacją prądów jonowych sodu

        u[1] to pochodna  dv/dt , gdzie v to potencjał błony neuronu
        */
        dudt[0] = a*(b*u[1] -u[0]);
        dudt[1] = 0.04 * Math.pow(u[1],2) + 5*u[1] +140-u[0]+I;
    }
}
