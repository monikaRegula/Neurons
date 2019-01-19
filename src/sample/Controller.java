package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import java.util.ArrayList;

public class Controller {


    @FXML
    private TextField txtA;

    @FXML
    private TextField txtB;

    @FXML
    private TextField txtC;

    @FXML
    private TextField txtD;

    @FXML
    private ScatterChart<Number,Number> chartU;

    @FXML
    private NumberAxis xAxis1;

    @FXML
    private NumberAxis yAxis1;

    @FXML
    private ScatterChart<Number,Number> chartV;

    @FXML
    private NumberAxis xAxis2;

    @FXML
    private NumberAxis yAxis2;

    @FXML
    private ScatterChart<Number,Number> chartI;

    @FXML
    private NumberAxis xAxis3;

    @FXML
    private NumberAxis yAxis3;

    @FXML
    private Button btn;

    @FXML
    private TextField txtF;

    @FXML
    private TextField txtVmax;

    @FXML
    private TextField txtVmean;

    @FXML
    private TextField txtVstd;



    private double a = 0;
    private double b = 0;
    private double c = 0;
    private double d = 0;


    @FXML
    void btnPressed(ActionEvent event) {

        initialize();

        clearOldData();

        if (txtA.getText().isEmpty() || txtB.getText().isEmpty() || txtC.getText().isEmpty() || txtD.getText().isEmpty() ) {

            throw new IllegalArgumentException("Wrong parameters");
        } else {
            a = Double.parseDouble(txtA.getText());
            b = Double.parseDouble(txtB.getText());
            c = Double.parseDouble(txtC.getText());
            d = Double.parseDouble(txtD.getText());
        }

        //wartości początkowe
        double I = 0;
        double v0 = c;
        double u0 = b * v0;

        //tworze podstawę neuronu
        FirstOrderDifferentialEquations spikeODE = new SpikeODE(a,b,I);
        //Tworzę integrator Eulera
        FirstOrderIntegrator integrator = new EulerIntegrator(0.01);
        //tworzę ścieżkę
        SpikePath spikePath = new SpikePath();
        //dodaje ścieżkędo integratora
        integrator.addStepHandler(spikePath);

        //granica błędu czasu
        double Tk = 0.001;
        //czas końcowy
        double te = 50;
        //czas odpowiadający 10% przedziału czasu
        double t10 = (0.10 * te);

        //warunki początkowe
        double[] yStart = new double[]{u0, v0};
        double[] yStop = new double[]{0, 1};

        //całkuje
        integrator.integrate(spikeODE, 0, yStart, t10, yStop);


        //jeśli czas całkowania przekroczy 15% wartości czasu końcowego +/- błąd to zachdzi zmiana prądu
        if ((spikePath.getTime() < (t10) + Tk) && (spikePath.getTime() > (t10) - Tk)) {
            System.out.println("Prąd się zmienia");

            I = 10;

            // ustawism nowe wartosci początkowe
            v0 = spikePath.getvValues().get(spikePath.getvValues().size() - 1);
            u0 = spikePath.getuValues().get(spikePath.getuValues().size() - 1);

            //nowa podstawa do liczenia ale ze zmienioną wartością prądu
            spikeODE = new SpikeODE(a, b, I);
            yStart = new double[]{u0, v0};
            yStop = new double[]{0, 1};

            //to jest EventHandler
            Spike spike = new Spike(c,d);
            //dodaje EventHandler do integratora
            integrator.addEventHandler(spike, 0.1, 0.001, 2000);
            //znowu całkuję
            integrator.integrate(spikeODE, t10, yStart, te, yStop);
        }


        //wrzucam do list tablicowych w Controllerze waryości ze ścieżki spikePath
        ArrayList<Double> uV = spikePath.getuValues();
        ArrayList<Double> vV = spikePath.getvValues();
        ArrayList<Double> time = spikePath.getTimes();

        //tu będą gromadone maksymalne wartości potencjału oraz odpowiadające im czasy
        ArrayList<Double> vMax = new ArrayList<>();
        ArrayList<Double> tMax = new ArrayList<>();

        //utowrzenie serii danych
        XYChart.Series<Number, Number> uSeries = new XYChart.Series();
        XYChart.Series<Number, Number> vSeries = new XYChart.Series();
        XYChart.Series<Number, Number> iSeries = new XYChart.Series();

        vSeries.setName("V Potencjał błony neuronu");
        uSeries.setName("U Zmienna odpowiadajaca za powrót do stanu spoczynkowego");
        iSeries.setName("I Natężenie prądu");


        for (int i = 0; i < uV.size(); i++) {
            if (time.get(i) > 7.5) iSeries.getData().add(new XYChart.Data<>(time.get(i), I));
            else iSeries.getData().add(new XYChart.Data<>(time.get(i), 0));
            uSeries.getData().add(new XYChart.Data<>(time.get(i), uV.get(i)));
            vSeries.getData().add(new XYChart.Data<>(time.get(i), vV.get(i)));

            if (vV.get(i) > 30) {
                vMax.add(vV.get(i));
                tMax.add(time.get(i));
            }

        }
        chartV.getData().add(vSeries);
        chartU.getData().add(uSeries);
        chartI.getData().add(iSeries);

        yAxis1.setTickUnit(1);
        yAxis1.setAutoRanging(true);
        xAxis1.setTickUnit(1);
        xAxis1.setAutoRanging(true);
        yAxis1.setLabel("U powrót do spoczynku ");
        xAxis1.setLabel("t");

        yAxis2.setTickUnit(1);
        yAxis2.setAutoRanging(true);
        xAxis2.setTickUnit(1);
        xAxis2.setAutoRanging(true);
        yAxis2.setLabel("Potencjał błonowy V [mV]");
        xAxis2.setLabel("t");

        yAxis3.setTickUnit(1);
        yAxis3.setAutoRanging(true);
        xAxis3.setTickUnit(1);
        xAxis3.setAutoRanging(true);
        yAxis3.setLabel("I Prąd synaptyczny [mV]");
        xAxis3.setLabel("t");


        //WYLICZANIE PARAMETRÓW OPISUJĄCYCH DYNAMIKĘ NEURONÓW

        double suma = 0;
        double mean = 0;
        double max = vMax.get(0);
        double std = 0;

        ArrayList<Double> times = new ArrayList<>();


        for (int i = 0; i < vMax.size(); i++) {
            //poszukuję MAKSYMALNEJ WARTOŚCI POTENCJAŁU IGLICY
            if (vMax.get(i) > max) max = vMax.get(i);

            suma += vMax.get(i);
            //obliczam CZAAS MIĘDZY MAKSYMALNYMI AMPLITUDAMI
            if (i > 0) {
                times.add(tMax.get(i) - tMax.get(i - 1));
            }
        }
        //obliczam ŚREDNI POTENCJAŁ IGLICY
        mean = suma / vMax.size();

        txtVmax.setText(String.valueOf(max));
        txtVmean.setText(String.valueOf(mean));

        double ssum = 0;
        double stdSum = 0;

        for (int i = 0; i < vMax.size(); i++) {
            stdSum += Math.pow(vMax.get(i) - mean, 2);
        }
        //obliczam ODCHYLENIE STANDARDOWE
        std = Math.pow((stdSum / (vMax.size() - 1)), 0.5);
        txtVstd.setText(String.valueOf(std));

        //obliczanie częstotliwości
        double T1 = te / vMax.size();
        double f1 = 1 / T1;
        txtF.setText(String.valueOf(f1));
    }

//metoda ustawia wartości parametrów
private void initialize(){
        txtA.setText("0.02");
        txtB.setText("0.2");
        txtC.setText("-65");
        txtD.setText("2");
}

//metoda czyści stare dane z wykresów
private void clearOldData(){
        chartV.getData().removeAll(chartV.getData());
        chartU.getData().removeAll(chartU.getData());
        chartI.getData().removeAll(chartI.getData());
}


}
