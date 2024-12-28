package steamTables;


import Exceptions.CannotBeInterpolated;
import Exceptions.MoreInfoNeeded;
import Exceptions.NotDefinedException;

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
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        double  T2=0;
        int row=0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == P ) {
                found =true;
                row=i;
                T2 = saturated[i][1];
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        if (T2 > T){ //CompressedLiquid
            found = false;
            double P2 = P;
            P /= 1000;
            if (P<5){
                saturated = db.getSaturatedTableT();
                for (int i = 0; i < saturated.length; i++) {
                    if ( saturated[i][0] == T ) {
                        found =true;
                        row=i;
                        P2 = saturated[i][1];
                        break;
                    }
                }
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                steam.setX(0);
                steam.setT(T);
                steam.setP(P2);
                steam.setV(saturated[row][2]);
                steam.setU(saturated[row][4]);
                steam.setH(saturated[row][7]);
                steam.setS(saturated[row][10]);
                return steam;
            }
            else {
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                double [][] compressed =  db.getCompressedLiquidTable();
                found =false;
                for (int i = 0; i < compressed.length; i++) {
                    if (compressed[i][0] == P && compressed[i][1] == T ) {
                        found =true;
                        row=i;
                        break;
                    }
                }
                if (!found) {
                    throw new NotDefinedException();
                }
                steam.setT(T);
                steam.setV(compressed[row][2]);
                steam.setU(compressed[row][3]);
                steam.setH(compressed[row][4]);
                steam.setS(compressed[row][5]);
                return steam;
            }
        }
        if (T2 == T){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
            return steam;
        }
        else {
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] superHeated = db.getSuperHeatedTable();
            found =false;
            P/=1000; // from Kpa to Mpa
            for (int i = 0; i < superHeated.length; i++) {
                if (superHeated[i][0] == P && superHeated[i][1] == T ) {
                    found =true;
                    row=i;
                    break;
                }
            }

            if (!found) {
                throw new NotDefinedException();
            }
            steam.setV(superHeated[row][2]);
            steam.setU(superHeated[row][3]);
            steam.setH(superHeated[row][4]);
            steam.setS(superHeated[row][5]);
            return steam;
        }

    }

    // Find Steam Using Temperature and Volume
    public Steam findTheSteamUsingTV(double T, double v) { // not completed
        Steam steam = new Steam();
        steam.setT(T);
        steam.setV(v);
        if (T >= steam.getCriticalTemperature()) { // superheated
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            steam.setX(1);
            double [][] SuperH = db.getSuperHeatedTable();
            boolean found = false;
            for (int i = 0; i < SuperH.length; i++) {
                if (SuperH[i][1] == T) {
                    found =true;
                    steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                    steam.setP(SuperH[i][0]);
                    steam.setU(SuperH[i][2]);
                    steam.setH(SuperH[i][3]);
                    steam.setS(SuperH[i][4]);
                    return steam;
                }
            }
        }
        else {
            double [][] saturated = db.getSaturatedTableT();
            boolean found = false;
            boolean f = false;
            boolean g = false;
            int row = 0;
            for (int i = 0; i < saturated.length; i++) {
                if ( saturated[i][0] == T ) {
                    found =true;
                    row=i;
                    break;
                }
                if (saturated[i][10] == v) {
                    found =true;
                    row=i;
                    g=true;
                    break;
                }
                if (saturated[i][12] == v) {
                    found =true;
                    row=i;
                    f=true;
                    break;
                }
            }
            if (!found) {
                throw new NotDefinedException();
            }
            steam.setP(saturated[row][1]);
            if (g){
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                steam.setX(1);
                steam.setV(saturated[row][2]);
                steam.setU(saturated[row][4]);
                steam.setH(saturated[row][7]);
                steam.setS(saturated[row][10]);
            }
            else if (f){
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                steam.setX(0);
                steam.setV(saturated[row][3]);
                steam.setU(saturated[row][6]);
                steam.setH(saturated[row][9]);
                steam.setS(saturated[row][12]);
            }
            else {
                double X = (v-saturated[row][2])/(saturated[row][3]-saturated[row][2]); //u = uf + X * ufg
                steam.setX(X);
                steam.setSteamPhase(SteamPhase.SaturatedMixture);
                double u =saturated[row][4] + X*(saturated[row][5]);
                double h = saturated[row][7] + X*(saturated[row][8]);
                double s = saturated[row][10] + X*(saturated[row][12]);
                steam.setV(v);
                steam.setU(u);
                steam.setH(h);
                steam.setS(s);
            }
        }

        return steam;
    }

    // Find Steam Using Temperature and Internal Energy
    public Steam findTheSteamUsingTU(double T, double u) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setU(u);
        double [][] saturated = db.getSaturatedTableT();
        boolean found = false;
        boolean f = false;
        boolean g = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == T ) {
                found =true;
                row=i;
                break;
            }
            if (saturated[i][10] == u) {
                found =true;
                row=i;
                g=true;
                break;
            }
            if (saturated[i][12] == u) {
                found =true;
                row=i;
                f=true;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setP(saturated[row][1]);
        if (g){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setX(1);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (f){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            double X = (u-saturated[row][4])/saturated[row][5] ; //u = uf + X * ufg
            steam.setX(X);
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][12]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }
        return steam;
    }

    // Find Steam Using Temperature and Enthalpy
    public Steam findTheSteamUsingTH(double T, double h) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setH(h);
        double [][] saturated = db.getSaturatedTableT();
        boolean found = false;
        boolean f = false;
        boolean g = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == T ) {
                found =true;
                row=i;
                break;
            }
            if (saturated[i][10] == h) {
                found =true;
                row=i;
                g=true;
                break;
            }
            if (saturated[i][12] == h) {
                found =true;
                row=i;
                f=true;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setP(saturated[row][1]);
        if (g){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setX(1);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (f){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            double X = (h-saturated[row][7])/saturated[row][8] ; //h = hf + X * hfg
            steam.setX(X);
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double s = saturated[row][10] + X*(saturated[row][12]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }
        return steam;
    }

    // Find Steam Using Temperature and Quality
    public Steam findTheSteamUsingTX(double T, double X) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setX(X);
        setPhase(steam, X);
        double [][] saturated = db.getSaturatedTableT();
        boolean found = false;
        double  T2=0;
        int row=0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == T ) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        if (X == 1.0){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setP(saturated[row][1]);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (X == 0.0){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setP(saturated[row][1]);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
            steam.setP(saturated[row][1]);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }

        return steam;
    }

    // Find Steam Using Temperature and Entropy
    public Steam findTheSteamUsingTS(double T, double s) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setS(s);
        double [][] saturated = db.getSaturatedTableT();
        boolean found = false;
        boolean f = false;
        boolean g = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == T ) {
                found =true;
                row=i;
                break;
            }
            if (saturated[i][10] == s) {
                found =true;
                row=i;
                g=true;
                break;
            }
            if (saturated[i][12] == s) {
                found =true;
                row=i;
                f=true;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setP(saturated[row][1]);
        if (g){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setX(1);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (f){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            double X = (s-saturated[row][10])/saturated[row][11] ; //s = sf + X * sfg
            steam.setX(X);
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
        }

        return steam;
    }

    public Steam findTheSteamUsingTPhase(double T, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setSteamPhase(phase);
        return steam;
    }

    // Find Steam Using Pressure and Volume
    public Steam findTheSteamUsingPV(double P, double v) {
        Steam steam = new Steam();
        steam.setP(P);
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        boolean f = false;
        boolean g = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == P ) {
                found =true;
                row=i;
                break;
            }
            if (saturated[i][10] == v) {
                found =true;
                row=i;
                g=true;
                break;
            }
            if (saturated[i][12] == v) {
                found =true;
                row=i;
                f=true;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setT(saturated[row][1]);
        if (g){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setX(1);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (f){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            double X = (v-saturated[row][2])/(saturated[row][3]-saturated[row][2]) ; //h = hf + X * hfg
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
            double h =saturated[row][7] + X*(saturated[row][8]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }

        return steam;
    }

    // Find Steam Using Pressure and Internal Energy
    public Steam findTheSteamUsingPU(double P, double u) { // tested and working 100%
        Steam steam = new Steam();
        steam.setP(P);
        steam.setU(u);
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        boolean f = false;
        boolean g = false;
        boolean m = false;
        boolean superHeated = false;
        boolean compressed = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if (saturated[i][0] == P) {
                found =true;
                row=i;
                break;
            }
            if (i!= saturated.length-1 && saturated[i][0] < P && saturated[i + 1][0] > P) { // interpolation
                found = true;
                double t1 = saturated[i][1], t2 = saturated[i + 1][1];
                double v1 = saturated[i][2], v2 = saturated[i + 1][2];
                double u1 = saturated[i][4], u2 = saturated[i + 1][4];
                double h1 = saturated[i][7], h2 = saturated[i + 1][7];
                double s1 = saturated[i][10], s2 = saturated[i + 1][10];

                double interpolatedT = Interpolation.linear(saturated[i][0], t1, saturated[i + 1][0], t2, P);
                double interpolatedV = Interpolation.linear(saturated[i][0], v1, saturated[i + 1][0], v2, P);
                double interpolatedU = Interpolation.linear(saturated[i][0], u1, saturated[i + 1][0], u2, P);
                double interpolatedH = Interpolation.linear(saturated[i][0], h1, saturated[i + 1][0], h2, P);
                double interpolatedS = Interpolation.linear(saturated[i][0], s1, saturated[i + 1][0], s2, P);

                steam.setT(interpolatedT);
                steam.setV(interpolatedV);
                steam.setU(interpolatedU);
                steam.setH(interpolatedH);
                steam.setS(interpolatedS);
                steam.setSteamPhase(SteamPhase.SaturatedMixture);
                return steam;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        if (saturated[row][4] == u) {
            f=true;
        }
        if (saturated[row][6] == u) {
            g=true;
        }
        if (saturated[row][4] < u && saturated[row][6] > u) {
            m=true;
        }
        if (saturated[row][6] < u) {
            superHeated=true;
        }
        if (saturated[row][4] > u) {
            compressed=true;
        }
        steam.setT(saturated[row][1]);
        if (f){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
            return steam;
        }
        else if (g){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
            return steam;
        }
        else if (m){
            double X = (u-saturated[row][4])/saturated[row][5] ; //u = uf + X * ufg
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }
        else if (superHeated){
            P/=1000;
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] table = db.getSuperHeatedTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][3] == u) {
                        steam.setT(table[i][1]);
                        steam.setV(table[i][2]);
                        steam.setH(table[i][4]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                    if (i!= table.length-1 &&table[i][3] < u && table[i + 1][3] > u) { // interpolation
                        double interpolatedT = Interpolation.linear(table[i][3], table[i][1], table[i + 1][3], table[i + 1][1], u);
                        double interpolatedV = Interpolation.linear(table[i][3], table[i][2], table[i + 1][3], table[i + 1][2], u);
                        double interpolatedH = Interpolation.linear(table[i][3], table[i][4], table[i + 1][3], table[i + 1][4], u);
                        double interpolatedS = Interpolation.linear(table[i][3], table[i][5], table[i + 1][3], table[i + 1][5], u);

                        steam.setT(interpolatedT);
                        steam.setV(interpolatedV);
                        steam.setH(interpolatedH);
                        steam.setS(interpolatedS);
                        return steam;
                    }
                }
            }
            throw new NotDefinedException();
        }
        else if (compressed){
            steam.setSteamPhase(SteamPhase.CompressedLiquid);
            double T =saturated[row][1];
            saturated = db.getSaturatedTableT();
            P/=1000;
            if (P<5){ // using saturated Tables
                steam.setT(T);
                for (int i =0 ;i<saturated.length;i++){
                    if (T == saturated[i][0]) {
                        steam.setT(saturated[i][0]);
                        steam.setP(saturated[i][1]);
                        steam.setV(saturated[i][2]);
                        steam.setU(saturated[i][4]);
                        steam.setH(saturated[i][7]);
                        steam.setS(saturated[i][10]);
                        return steam;
                    }

                    if (i!= saturated.length-1 && saturated[i][0] < T && saturated[i + 1][0] > T) { // interpolation
                        double p1 = saturated[i][1], p2 = saturated[i + 1][1];
                        double v1 = saturated[i][2], v2 = saturated[i + 1][2];
                        double u1 = saturated[i][4], u2 = saturated[i + 1][4];
                        double h1 = saturated[i][7], h2 = saturated[i + 1][7];
                        double s1 = saturated[i][10], s2 = saturated[i + 1][10];
                        double interpolatedP = Interpolation.linear(saturated[i][0], p1, saturated[i + 1][0], p2, T);
                        double interpolatedV = Interpolation.linear(saturated[i][0], v1, saturated[i + 1][0], v2, T);
                        double interpolatedU = Interpolation.linear(saturated[i][0], u1, saturated[i + 1][0], u2, T);
                        double interpolatedH = Interpolation.linear(saturated[i][0], h1, saturated[i + 1][0], h2, T);
                        double interpolatedS = Interpolation.linear(saturated[i][0], s1, saturated[i + 1][0], s2, T);
                        steam.setP(interpolatedP);
                        steam.setV(interpolatedV);
                        steam.setU(interpolatedU);
                        steam.setH(interpolatedH);
                        steam.setS(interpolatedS);
                        return steam;
                    }

                }
                throw new NotDefinedException();
            }
            double [][] table = db.getCompressedLiquidTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][3] == u) {
                        steam.setT(table[i][1]);
                        steam.setV(table[i][2]);
                        steam.setH(table[i][4]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                    if (i!= table.length-1 &&table[i][3] < u && table[i + 1][3] > u) { // interpolation
                        double interpolatedT = Interpolation.linear(table[i][3], table[i][1], table[i + 1][3], table[i + 1][1], u);
                        double interpolatedV = Interpolation.linear(table[i][3], table[i][2], table[i + 1][3], table[i + 1][2], u);
                        double interpolatedH = Interpolation.linear(table[i][3], table[i][4], table[i + 1][3], table[i + 1][4], u);
                        double interpolatedS = Interpolation.linear(table[i][3], table[i][5], table[i + 1][3], table[i + 1][5], u);

                        steam.setT(interpolatedT);
                        steam.setV(interpolatedV);
                        steam.setH(interpolatedH);
                        steam.setS(interpolatedS);
                        return steam;
                    }
                }

            }
        }
        return steam;
    }

    // Find Steam Using Pressure and Enthalpy
    public Steam findTheSteamUsingPH(double P, double h) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setH(h);
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        boolean f = false;
        boolean g = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == P ) {
                found =true;
                row=i;
                break;
            }
            if (saturated[i][10] == h) {
                found =true;
                row=i;
                g=true;
                break;
            }
            if (saturated[i][12] == h) {
                found =true;
                row=i;
                f=true;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setT(saturated[row][1]);
        if (g){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setX(1);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (f){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            double X = (h-saturated[row][7])/saturated[row][8] ; //h = hf + X * hfg
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }

        return steam;
    }

    public Steam findTheSteamUsingPPhase(double P, SteamPhase steamPhase) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setSteamPhase(steamPhase);
        return steam;
    }

    // Find Steam Using Pressure and Quality
    public Steam findTheSteamUsingPX(double P, double X) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setX(X);
        setPhase(steam, X);
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == P ) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        if (X == 1.0){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setT(saturated[row][1]);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (X == 0.0){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setT(saturated[row][1]);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setT(saturated[row][1]);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }

        return steam;
    }

    // Find Steam Using Pressure and Entropy
    public Steam findTheSteamUsingPS(double P, double s) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setS(s);
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        boolean f = false;
        boolean g = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == P ) {
                found =true;
                row=i;
                break;
            }
            if (saturated[i][10] == s) {
                found =true;
                row=i;
                g=true;
                break;
            }
            if (saturated[i][12] == s) {
                found =true;
                row=i;
                f=true;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setT(saturated[row][1]);
        if (g){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setX(1);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (f){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            double X = (s-saturated[row][10])/saturated[row][11] ; //s = sf + X * sfg
            steam.setX(X);
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
        }

        return steam;
    }

    public Steam findTheSteamUsingUV(double u, double v) {
        Steam steam = new Steam();
        steam.setV(v);
        steam.setU(u);
        return steam;
    }

    public Steam findTheSteamUsingUX(double u, double X) { // is it neccessry
        Steam steam = new Steam();
        steam.setX(X);
        steam.setU(u);
        setPhase(steam, X);
        double [][] saturated = db.getSaturatedTableP();
        boolean Liquid = false;
        boolean Vapor = false;
        boolean Mixture = false;
        boolean found = false;
        boolean secondTry = false;
        if (X ==1){
            Vapor = true;
        }
        else if (X==0){
            Liquid = true;
        }
        else {
            Mixture = true;
        }
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][4] == u ) {
                found =true;
                row=i;
                break;
            }
            if ( saturated[i][6] == u ) {
                found =true;
                row=i;
                break;
            }
            if ( saturated[i][4] < u && u < saturated[i][6] ) {
                found = true;
                row=i;
                break;
            }
        }
        if (!found) {
            secondTry = true;
            saturated = db.getSaturatedTableT();
            Liquid = false;
            Vapor = false;
            Mixture = false;
            found = false;
            if (X ==1){
                Vapor = true;
            }
            else if (X==0){
                Liquid = true;
            }
            else if (X>1 || X<0) {
                Mixture = true;
            }
            row = 0;
            for (int i = 0; i < saturated.length; i++) {
                if ( saturated[i][4] == u ) {
                    found =true;
                    row=i;
                    break;
                }
                if ( saturated[i][6] == u ) {
                    found =true;
                    row=i;
                    break;
                }
                if ( saturated[i][4] < u && u < saturated[i][6] ) {
                    found = true;
                    row=i;
                    break;
                }
            }
            if (!found) {
                throw new NotDefinedException();
            }
        }
        if (!secondTry){
            steam.setT(saturated[row][1]);
            steam.setP(saturated[row][0]);
        }
        else {
            steam.setT(saturated[row][0]);
            steam.setP(saturated[row][1]);
        }
        if (Vapor){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (Liquid){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else if (Mixture) {
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }
        else {
            throw new IllegalArgumentException("X should be between 0 and 1");
        }

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
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
        }
    }

    public Steam findTheSteamUsingTState(double T, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setSteamPhase(phase);
        steam.setX(phase.getX());
        double X = steam.getX();
        double saturated[][] = db.getSaturatedTableT();
        boolean found = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == T ) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setT(saturated[row][0]);
        steam.setP(saturated[row][1]);
        if (steam.getX()==1){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (steam.getX()==0){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }
        return steam;

    }

    public Steam findTheSteamUsingPState(double P, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setSteamPhase(phase);
        steam.setX(phase.getX());
        double X = steam.getX();
        double saturated[][] = db.getSaturatedTableP();
        boolean found = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if ( saturated[i][0] == P ) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found) {
            throw new NotDefinedException();
        }
        steam.setT(saturated[row][1]);
        steam.setP(saturated[row][0]);
        if (steam.getX()==1){
            steam.setSteamPhase(SteamPhase.SaturatedVapour);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else if (steam.getX()==0){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else {
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double u = saturated[row][4] + X*(saturated[row][5]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setV(v);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
        }
        return steam;

    }

    public Steam findTheSteamUsingHPhase(double H, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setH(H);
        steam.setSteamPhase(phase);
        steam.setX(phase.getX());
        return steam;
    }

    public Steam findTheSteamUsingSPhase(double S, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setS(S);
        steam.setSteamPhase(phase);
        steam.setX(phase.getX());
        return steam;
    }

    public Steam findTheSteamUsingHX(double H, double X) {
        Steam steam = new Steam();
        return steam;
    }

    public Steam findTheSteamUsingUPhase(double U, SteamPhase phase) {
        return new Steam();
    }


    public Steam findTheSteamUsingXS(double X, double s) {
        return findTheSteamUsingSX(s,X);
    }

    public Steam findTheSteamUsingVPhase(double v, SteamPhase phase) {
        Steam steam = new Steam();
        return steam;
    }

    public Steam findTheSteamUsingVX(double v, double X) { // need to be Tested .... nicht mix
        Steam steam = new Steam();
        steam.setV(v);
        steam.setX(X);
        int index = X==0? 2 : X==1? 3 : -1;
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        int row=0;
        if (index != -1) {
            for (int i = 0; i < saturated.length; i++) {
                if (saturated[i][index] == v) {
                    found = true;
                    row = i;
                    break;
                }
            }
            if (!found) {
                throw new NotDefinedException();
            }
            steam.setP(saturated[row][0]);
            steam.setT(saturated[row][1]);
            if (steam.getX()==1){
                steam.setU(saturated[row][6]);
                steam.setH(saturated[row][9]);
                steam.setS(saturated[row][12]);
            }
            else if (steam.getX()==0){
                steam.setU(saturated[row][4]);
                steam.setH(saturated[row][7]);
                steam.setS(saturated[row][10]);
            }
        }
        else {
            throw new NotDefinedException();
        }
        return steam;
    }

    public Steam findTheSteamUsingSX(double S, double X) { //done Sat    nicht mix
        Steam steam = new Steam();
        steam.setS(S);
        steam.setX(X);
        int index = X==0? 10 : X==1? 12 : -1;
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        int row=0;
        if (index != -1) {
            for (int i = 0; i < saturated.length; i++) {
                if (saturated[i][index] == S) {
                    found = true;
                    row = i;
                    break;
                }
            }
            if (!found) {
                throw new NotDefinedException();
            }
            steam.setP(saturated[row][0]);
            steam.setT(saturated[row][1]);
            if (steam.getX()==1){
                steam.setV(saturated[row][3]);
                steam.setU(saturated[row][6]);
                steam.setH(saturated[row][9]);
            }
            else if (steam.getX()==0){
                steam.setV(saturated[row][2]);
                steam.setU(saturated[row][4]);
                steam.setH(saturated[row][7]);
            }
        }
        else {
            throw new MoreInfoNeeded("SX is not defined");
        }
        return steam;
    }
    public static void main(String args []){
        Controller controller = new Controller();
        Steam steam = controller.findTheSteamUsingTP(120.21, 200);
        System.out.println(steam.toString());
    }

    public DataBase getDb() {
        return db;
    }

    public Steam findTheSteamUsingUS(double u, double s) {
        Steam steam = new Steam();
        steam.setU(u);
        steam.setS(s);
        return steam;
    }

    public Steam findTheSteamUsingUH(double u, double h) {
        Steam steam = new Steam();
        steam.setH(h);
        steam.setU(u);
        return steam;
    }

    public Steam findTheSuperHeatedSteamOrCompressedLiquid(double x, double y, int i, int j, SteamPhase phase) { // handle Mpa
        Steam steam = new Steam();
        steam.setSteamPhase(phase);
        System.out.println(x);
        System.out.println(y);
        System.out.println(i);
        System.out.println(j);
        double [][] table = db.getSuperHeatedTable();
        if (phase == SteamPhase.CompressedLiquid){
            table = db.getCompressedLiquidTable();
            if (i==0){
                if (x<5){
                    throw new NotDefinedException();
                }
            }
            if (j==0){
                if (y<5){
                    throw new NotDefinedException();
                }
            }
        }
        for (int k=0 ;k<table.length;k++) { //found or interpolation
            if (table[k][i] == x && table[k][j] == y) {
                steam.setP(table[k][0]);
                steam.setT(table[k][1]);
                steam.setV(table[k][2]);
                steam.setU(table[k][3]);
                steam.setH(table[k][4]);
                steam.setS(table[k][5]);
                return steam;
            }
        }
        for (int k=0 ;k<table.length;k++) { //interpolation
            if (table[k][i] > x && i==0) { // when it's pressure can't do interpolation
                break;
            }
            if (table[k][j] > y && j==0) { // when it's pressure can't do interpolation
                break;
            }
            if (k != table.length - 1 && table[k][i] < x && table[k + 1][j] > x) { //interpolation
                double p1 = table[k][0], p2 = table[k + 1][0];
                double t1 = table[k][1], t2 = table[k + 1][1];
                double v1 = table[k][2], v2 = table[k + 1][2];
                double u1 = table[k][3], u2 = table[k + 1][3];
                double h1 = table[k][4], h2 = table[k + 1][4];
                double s1 = table[k][5], s2 = table[k + 1][5];
                double interpolatedP = 0, interpolatedT = 0, interpolatedV = 0, interpolatedU = 0, interpolatedH = 0, interpolatedS = 0;
                try {
                    interpolatedP = Interpolation.linear(table[k][0], p1, table[k + 1][0], p2, x);
                } catch (CannotBeInterpolated e) {
                    interpolatedP = p1;
                }
                try {
                    interpolatedT = Interpolation.linear(table[k][0], t1, table[k + 1][0], t2, x);
                } catch (CannotBeInterpolated e) {
                    interpolatedT = t1;
                }
                try {
                    interpolatedV = Interpolation.linear(table[k][0], v1, table[k + 1][0], v2, x);
                } catch (CannotBeInterpolated e) {
                    interpolatedV = v1;
                }
                try {
                    interpolatedU = Interpolation.linear(table[k][0], u1, table[k + 1][0], u2, x);
                } catch (CannotBeInterpolated e) {
                    interpolatedU = u1;
                }
                try {
                    interpolatedH = Interpolation.linear(table[k][0], h1, table[k + 1][0], h2, x);
                } catch (CannotBeInterpolated e) {
                    interpolatedH = h1;
                }
                try {
                    interpolatedS = Interpolation.linear(table[k][0], s1, table[k + 1][0], s2, x);
                } catch (CannotBeInterpolated e) {
                    interpolatedS = s1;
                }
                steam.setP(interpolatedP);
                steam.setT(interpolatedT);
                steam.setV(interpolatedV);
                steam.setU(interpolatedU);
                steam.setH(interpolatedH);
                steam.setS(interpolatedS);
                return steam;
                }
            }
        throw new NotDefinedException(phase +" is not defined");
    }

}

