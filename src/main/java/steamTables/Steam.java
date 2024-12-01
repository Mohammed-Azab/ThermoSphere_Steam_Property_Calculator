package steamTables;

public class Steam extends Water{
    private double T;
    private double P;
    private double v;
    private double X;
    private double u;
    private double h;
    private double s;

    SteamPhase steamPhase;

    public Steam() {
        super();
        steamPhase = SteamPhase.values()[0];
        X=steamPhase.getX();
    }

    public double getT() {
        return T;
    }
    public void setT(double t) {
        T = t;
    }
    public double getP() {
        return P;
    }
    public void setP(double p) {
        P = p;
    }
    public double getV() {
        return v;
    }
    public void setV(double v) {
        this.v = v;
    }
    public double getU() {
        return u;
    }
    public void setU(double u) {
        this.u = u;
    }
    public double getH() {
        return h;
    }
    public void setH(double h) {
        this.h = h;
    }
    public double getS() {
        return s;
    }
    public void setS(double s) {
        this.s = s;
    }

    public void setX(double x) {
        X = x;
    }
    public double getX() {
        return X;
    }
    public SteamPhase getSteamPhase() {
        return steamPhase;
    }
    public void setSteamPhase(SteamPhase steamPhase) {
        this.steamPhase = steamPhase;
        X=steamPhase.getX();
    }

    @Override
    public String toString(){
        return "T: "+T+" P: "+P+" V: "+v+" X: "+X+" U: "+u+" H: "+h+" S: "+s+" SteamPhase: "+steamPhase;
    }


}
