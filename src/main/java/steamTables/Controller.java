package steamTables;


public class Controller {

    private static DataBase db ;


    public Controller() {
        db = new DataBase();
    }

    // Find Steam Using Temperature and Pressure
    public Steam findTheSteamUsingTP(double T, double P) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setP(P);
        return steam;
    }

    // Find Steam Using Temperature and Volume
    public Steam findTheSteamUsingTV(double T, double v) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setV(v);

        return steam;
    }

    // Find Steam Using Temperature and Internal Energy
    public Steam findTheSteamUsingTU(double T, double u) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setU(u);
        return steam;
    }

    // Find Steam Using Temperature and Enthalpy
    public Steam findTheSteamUsingTH(double T, double h) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setH(h);

        return steam;
    }

    // Find Steam Using Temperature and Quality
    public Steam findTheSteamUsingTX(double T, double X) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setX(X);
        setPhase(steam, X);

        return steam;
    }

    // Find Steam Using Temperature and Entropy
    public Steam findTheSteamUsingTS(double T, double s) {
        Steam steam = new Steam();
        steam.setT(T);
        return steam;
    }

    // Find Steam Using Pressure and Volume
    public Steam findTheSteamUsingPV(double P, double v) {
        Steam steam = new Steam();
        steam.setP(P);

        return steam;
    }

    // Find Steam Using Pressure and Internal Energy
    public Steam findTheSteamUsingPU(double P, double u) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setU(u);
        return steam;
    }

    // Find Steam Using Pressure and Enthalpy
    public Steam findTheSteamUsingPH(double P, double h) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setH(h);


        return steam;
    }

    // Find Steam Using Pressure and Quality
    public Steam findTheSteamUsingPX(double P, double X) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setX(X);
        setPhase(steam, X);

        return steam;
    }

    // Find Steam Using Pressure and Entropy
    public Steam findTheSteamUsingPS(double P, double s) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setS(s);

        return steam;
    }

    public Steam findTheSteamUsingUV(double u, double v) {
        Steam steam = new Steam();
        steam.setV(v);
        steam.setU(u);
        return steam;
    }

    public Steam findTheSteamUsingUX(double u, double X) {
        Steam steam = new Steam();
        steam.setX(X);
        steam.setU(u);
        setPhase(steam, X);
        return steam;
    }

    private void setPhase(Steam steam, double X) {
        if (X ==1) {
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
        }
        else if (X ==0) {
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
        }
        else if ( X > 1 || X < 0 ) {
            throw new IllegalArgumentException("X must be between 0 and 1");
        }
        else {
            steam.setSteamPhase(SteamPhase.saturatedMixture);
        }
    }

    public Steam findTheSteamUsingTState(double T, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setSteamPhase(phase);
        steam.setX(phase.getX());
        return steam;

    }

    public Steam findTheSteamUsingPState(double P, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setSteamPhase(phase);
        steam.setX(phase.getX());
        return steam;

    }

    public Steam findTheSteamUsingHState(double H, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setH(H);
        steam.setSteamPhase(phase);
        steam.setX(phase.getX());
        return steam;

    }



}
