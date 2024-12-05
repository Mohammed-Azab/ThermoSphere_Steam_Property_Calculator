package steamTables;


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
            if (P<5){
                saturated = db.getSaturatedTableT();
                for (int i = 0; i < saturated.length; i++) {
                    if ( saturated[i][0] == T ) {
                        System.out.println(saturated[i][0]);
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
            }
            else {
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                steam.setX(0);
                double [][] compressed=  db.getCompressedLiquidTable();
                found =false;
                for (int i = 0; i < compressed.length; i++) {
                    if (compressed[i][0] == P ) {
                        found =true;
                        row=i;
                        break;
                    }
                }
                if (!found) {
                    throw new NotDefinedException();
                }
                steam.setX(0);
                steam.setT(T);
                steam.setV(compressed[row][2]);
                steam.setU(compressed[row][3]);
                steam.setH(compressed[row][4]);
                steam.setS(compressed[row][5]);
            }
        }
        if (T2 == T){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setX(0);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else {
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            steam.setX(1);
            double [][] superHeated = db.getSuperHeatedTable();
            found =false;
            for (int i = 0; i < superHeated.length; i++) {
                if (superHeated[i][0] == P ) {
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
        }

        return steam;
    }

    // Find Steam Using Temperature and Volume
    public Steam findTheSteamUsingTV(double T, double v) { // not completed
        Steam steam = new Steam();
        steam.setT(T);
        steam.setV(v);
        if (T >= steam.getCriticalTemperature()) { // superheated
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            steam.setX(1);
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
    public Steam findTheSteamUsingPU(double P, double u) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setU(u);
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
    public Steam findTheSteamUsingHX(double H, double X) {
        Steam steam = new Steam();
        return steam;
    }

    public Steam findTheSteamUsingHState(double H, SteamPhase phase) {
        return findTheSteamUsingHX(H, phase.getX());

    }
    public Steam findTheSteamUsingUState(double U, SteamPhase phase) {
        return findTheSteamUsingUX(phase.getX(), U);
    }

    public static void main(String args []){
        Controller controller = new Controller();
        Steam steam = controller.findTheSteamUsingTP(120.21, 200);
        System.out.println(steam.toString());
    }





}
