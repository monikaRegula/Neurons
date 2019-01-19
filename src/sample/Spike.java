package sample;

import org.apache.commons.math3.ode.events.EventHandler;

public class Spike implements EventHandler {

    //atrybuty klasy:
    //sign służy do zainicjowania nowego stanu zmienia się na znak przeciwny
    private int sign;
    //c opisuje wartość powrotną potenjału błonowego v po osiągnięciu maksimum
    private double c;
   // d opisuje powrót zmiennej odpowiadającej za powrót błony do stanu spoczynkowego
    private double d;


    public Spike(double c, double d) {
        this.c = c;
        this.d = d;
    }

    //metoda inicjalizuje EventHandler
    @Override
    public void init(double t, double[] u, double dudt) {
        sign = 1;
    }

    //metoda szuka zera jednak w naszym przypadku jest to wartość potencjału równa 30 mV
    @Override
    public double g(double t, double[] u) {
        return sign* (u[1] -30);
    }

    //metoda zapoczatkowuje nastepny bodziec
    @Override
    public Action eventOccurred(double t, double[] u, boolean b) {
        sign = -sign;
        return Action.RESET_STATE;
    }

    //tu dochodzi do depolaryzacji
    @Override
    public void resetState(double t, double[] u) {
        //z warunku
        u[1] = c;
        u[0] = u[0] +d;
    }
}
