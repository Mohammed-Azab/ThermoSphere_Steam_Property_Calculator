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
    public Steam findTheSteamUsingTP(double T, double P) { // all working
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
            for (int i = 0; i < saturated.length - 1; i++) {
                if (saturated[i][0] < P && saturated[i + 1][0] > P) { // Interpolation
                    double interpolatedU = Interpolation.linear(saturated[i][0], saturated[i][4],
                            saturated[i + 1][0], saturated[i + 1][4], P);
                    double interpolatedV = Interpolation.linear(saturated[i][0], saturated[i][2],
                            saturated[i + 1][0], saturated[i + 1][2], P);
                    double interpolatedH = Interpolation.linear(saturated[i][0], saturated[i][7],
                            saturated[i + 1][0], saturated[i + 1][7], P);
                    double interpolatedS = Interpolation.linear(saturated[i][0], saturated[i][10],
                            saturated[i + 1][0], saturated[i + 1][10], P);

                    steam.setU(interpolatedU);
                    steam.setV(interpolatedV);
                    steam.setH(interpolatedH);
                    steam.setS(interpolatedS);
                    steam.setP(P);
                    steam.setT(T);
                    return steam;
                }
            }
            throw new NotDefinedException("Pressure value not found for interpolation.");
        }
        if (T2 > T){ //CompressedLiquid
            steam.setSteamPhase(SteamPhase.CompressedLiquid);
            double P2 = P;
            P /= 1000;
            if (P<5){
                saturated = db.getSaturatedTableT();
                for (int i = 0; i < saturated.length; i++) {
                    if ( saturated[i][0] == T ) {
                        row=i;
                        steam.setT(T);
                        steam.setP(P*1000);
                        steam.setV(saturated[row][2]);
                        steam.setU(saturated[row][4]);
                        steam.setH(saturated[row][7]);
                        steam.setS(saturated[row][10]);
                        return steam;
                    }
                }
                for (int i = 0; i < saturated.length - 1; i++) {
                    if (saturated[i][0] < T && saturated[i + 1][0] > T) { // Interpolation
                        double interpolatedU = Interpolation.linear(saturated[i][0], saturated[i][4],  saturated[i + 1][0], saturated[i + 1][4], T);
                        double interpolatedV = Interpolation.linear(saturated[i][0], saturated[i][2],  saturated[i + 1][0], saturated[i + 1][2], T);
                        double interpolatedH = Interpolation.linear(saturated[i][0], saturated[i][7], saturated[i + 1][0],  saturated[i + 1][7], T);
                        double interpolatedS = Interpolation.linear(saturated[i][0], saturated[i][10], saturated[i + 1][0], saturated[i + 1][10],T);
                        steam.setU(interpolatedU);
                        steam.setV(interpolatedV);
                        steam.setH(interpolatedH);
                        steam.setS(interpolatedS);
                        steam.setP(P);
                        steam.setT(T);
                        return steam;
                    }
                }
            }
            else {
                double [][] compressed =  db.getCompressedLiquidTable();
                for (int i = 0; i < compressed.length; i++) {
                    if (compressed[i][0] == P && compressed[i][1] == T ) {
                        row=i;
                        steam.setT(T);
                        steam.setV(compressed[row][2]);
                        steam.setU(compressed[row][3]);
                        steam.setH(compressed[row][4]);
                        steam.setS(compressed[row][5]);
                        return steam;
                    }
                }
                steam = interpolatedSuperHeatedOrCompressed(T, P, "T", "P", compressed);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
        }
        if (T2 == T){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
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
                    steam.setV(superHeated[row][2]);
                    steam.setU(superHeated[row][3]);
                    steam.setH(superHeated[row][4]);
                    steam.setS(superHeated[row][5]);
                    return steam;
                }
            }

            if (!found) {
                steam= interpolatedSuperHeatedOrCompressed(T,P,"T","P",superHeated);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }

        }
        throw new NotDefinedException("Pressure value not found for interpolation with Temperature.");

    }

    // Find Steam Using Temperature and Volume
    public Steam findTheSteamUsingTV(double T, double v) { // it's working
        Steam steam = new Steam();
        steam.setT(T);
        steam.setV(v);
        if (T >= steam.getCriticalTemperature()-273 || T > 380) { // superheated
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] SuperH = db.getSuperHeatedTable();
            boolean found = false;
            for (int i = 0; i < SuperH.length; i++) {
                if (SuperH[i][1] == T && SuperH[i][2] == v) {
                    found =true;
                    steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                    steam.setP(SuperH[i][0]);
                    steam.setU(SuperH[i][3]);
                    steam.setH(SuperH[i][4]);
                    steam.setS(SuperH[i][5]);
                    return steam;
                }
            }
            if (!found) {
                throw new NotDefinedException("TV");
            }
        }
        else {
            double [][] saturated = db.getSaturatedTableT();
            boolean found = false;
            boolean f = false;
            boolean g = false;
            int row = 0;
            double vf =0;
            for (int i = 0; i < saturated.length; i++) {
                if ( saturated[i][0] == T ) {
                    found =true;
                    row=i;
                    vf =saturated[i][2];
                    if (saturated[i][3] == v) {
                        found =true;
                        row=i;
                        g=true;
                        break;
                    }
                    if (saturated[i][2] == v) {
                        found =true;
                        row=i;
                        f=true;
                        break;
                    }
                }
            }
            if (!found) {
                return interpolatedSatT(T, v, "T", "V", saturated);
            }
            steam.setP(saturated[row][1]);
            if (g){
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                steam.setU(saturated[row][6]);
                steam.setH(saturated[row][9]);
                steam.setS(saturated[row][12]);
                return steam;
            }
            else if (f){
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                steam.setU(saturated[row][4]);
                steam.setH(saturated[row][7]);
                steam.setS(saturated[row][10]);
                return steam;
            }
            else if (saturated[row][2] < v && (saturated[row][3] > v)) {
                double X = (v-saturated[row][2])/(saturated[row][3]-saturated[row][2]); //u = uf + X * ufg
                steam.setX(X);
                steam.setSteamPhase(SteamPhase.SaturatedMixture);
                double u =saturated[row][4] + X*(saturated[row][5]);
                double h = saturated[row][7] + X*(saturated[row][8]);
                double s = saturated[row][10] + X*(saturated[row][11]);
                steam.setV(v);
                steam.setU(u);
                steam.setH(h);
                steam.setS(s);
                return steam;
            }
            else {
                double [][] table = db.getSuperHeatedTable();
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                if (vf < v) { //compressed
                    table = db.getCompressedLiquidTable();
                    steam.setSteamPhase(SteamPhase.CompressedLiquid);
                }
                for (int i = 0; i < table.length; i++) {
                    if (table[i][1] == T && table[i][2] == v) {
                        steam.setU(table[i][3]);
                        steam.setH(table[i][4]);
                        steam.setS(table[i][5]);
                        steam.setP(table[i][0] * 1000);
                        return steam;
                    }
                }
                throw new NotDefinedException("TV");
                }
            }
        return steam;
    }

    // Find Steam Using Temperature and Internal Energy
    public Steam findTheSteamUsingTU(double T, double u) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setU(u);
        if (T >= steam.getCriticalTemperature()-273 || T > 380) { // superheated
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] SuperH = db.getSuperHeatedTable();
            boolean found = false;
            for (int i = 0; i < SuperH.length; i++) {
                if (SuperH[i][1] == T && SuperH[i][3] == u) {
                    found =true;
                    steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                    steam.setP(SuperH[i][0]);
                    steam.setV(SuperH[i][2]);
                    steam.setH(SuperH[i][4]);
                    steam.setS(SuperH[i][5]);
                    return steam;
                }
            }
            if (!found) {
                throw new NotDefinedException("TU");
            }
        }
        else {
            double [][] saturated = db.getSaturatedTableT();
            boolean found = false;
            boolean f = false;
            boolean g = false;
            int row = 0;
            double uf=0;
            for (int i = 0; i < saturated.length; i++) {
                if ( saturated[i][0] == T ) {
                    found =true;
                    row=i;
                    uf =saturated[i][4];
                    if (saturated[i][6] == u) {
                        found =true;
                        row=i;
                        g=true;
                        break;
                    }
                    if (saturated[i][4] == u) {
                        found =true;
                        row=i;
                        f=true;
                        break;
                    }
                }
            }
            if (!found) {
                return interpolatedSatT(T, u, "T", "U", saturated);
            }
            steam.setP(saturated[row][1]);
            if (g){
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                steam.setV(saturated[row][3]);
                steam.setH(saturated[row][9]);
                steam.setS(saturated[row][12]);
                return steam;
            }
            else if (f){
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                steam.setV(saturated[row][2]);
                steam.setH(saturated[row][7]);
                steam.setS(saturated[row][10]);
                return steam;
            }
            else if (saturated[row][4] < u && (saturated[row][6] > u)) {
                double X = (u-saturated[row][4])/(saturated[row][5]); //u = uf + X * ufg
                steam.setX(X);
                steam.setSteamPhase(SteamPhase.SaturatedMixture);
                double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
                double h = saturated[row][7] + X*(saturated[row][8]);
                double s = saturated[row][10] + X*(saturated[row][11]);
                steam.setV(v);
                steam.setH(h);
                steam.setS(s);
                return steam;
            }
            else {
                double [][] table = db.getSuperHeatedTable();
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                if (uf < u) { //compressed
                    table = db.getCompressedLiquidTable();
                    steam.setSteamPhase(SteamPhase.CompressedLiquid);
                }
                for (int i = 0; i < table.length; i++) {
                    if (table[i][1] == T && table[i][3] == u) {
                        steam.setV(table[i][2]);
                        steam.setH(table[i][4]);
                        steam.setS(table[i][5]);
                        steam.setP(table[i][0] * 1000);
                        return steam;
                    }
                }
                throw new NotDefinedException("TU");
            }
        }
        return steam;
    }

    // Find Steam Using Temperature and Enthalpy
    public Steam findTheSteamUsingTH(double T, double h) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setH(h);
        if (T >= steam.getCriticalTemperature()-273 || T > 380) { // superheated
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] SuperH = db.getSuperHeatedTable();
            boolean found = false;
            for (int i = 0; i < SuperH.length; i++) {
                if (SuperH[i][1] == T && SuperH[i][4] == h) {
                    found =true;
                    steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                    steam.setP(SuperH[i][0]);
                    steam.setV(SuperH[i][2]);
                    steam.setU(SuperH[i][3]);
                    steam.setS(SuperH[i][5]);
                    return steam;
                }
            }
            if (!found) {
                throw new NotDefinedException("TH");
            }
        }
        else {
            double [][] saturated = db.getSaturatedTableT();
            boolean found = false;
            boolean f = false;
            boolean g = false;
            int row = 0;
            double hf=0;
            for (int i = 0; i < saturated.length; i++) {
                if ( saturated[i][0] == T ) {
                    found =true;
                    row=i;
                    hf =saturated[i][7];
                    if (saturated[i][9] == h) {
                        found =true;
                        row=i;
                        g=true;
                        break;
                    }
                    if (saturated[i][7] == h) {
                        found =true;
                        row=i;
                        f=true;
                        break;
                    }
                }
            }
            if (!found) {
                return interpolatedSatT(T, h, "T", "H", saturated);
            }
            steam.setP(saturated[row][1]);
            if (g){
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                steam.setV(saturated[row][3]);
                steam.setU(saturated[row][6]);
                steam.setS(saturated[row][12]);
                return steam;
            }
            else if (f){
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                steam.setV(saturated[row][2]);
                steam.setU(saturated[row][4]);
                steam.setS(saturated[row][10]);
                return steam;
            }
            else if (saturated[row][7] < h && (saturated[row][9] > h)) {
                double X = (h-saturated[row][7])/(saturated[row][8]); //u = uf + X * ufg
                steam.setX(X);
                steam.setSteamPhase(SteamPhase.SaturatedMixture);
                double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
                double u = saturated[row][4] + X*(saturated[row][5]);
                double s = saturated[row][10] + X*(saturated[row][11]);
                steam.setV(v);
                steam.setU(u);
                steam.setS(s);
                return steam;
            }
            else {
                System.out.println("Entered");
                double [][] table = hf < h? db.getSuperHeatedTable(): db.getCompressedLiquidTable();
                steam.setSteamPhase(hf < h? SteamPhase.SuperHeatedWater: SteamPhase.CompressedLiquid);
                System.out.println(steam.getSteamPhase());
                for (int i = 0; i < table.length; i++) {
                    if (table[i][1] == T && table[i][4] == h) {
                        steam.setV(table[i][2]);
                        steam.setU(table[i][3]);
                        steam.setS(table[i][5]);
                        steam.setP(table[i][0]*1000);
                        return steam;
                    }
                }
                throw new NotDefinedException("TH");
            }
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
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else if (X == 0.0){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setP(saturated[row][1]);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
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
        if (T >= steam.getCriticalTemperature()-273 || T > 380) { // superheated
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] SuperH = db.getSuperHeatedTable();
            boolean found = false;
            for (int i = 0; i < SuperH.length; i++) {
                if (SuperH[i][1] == T && SuperH[i][5] == s) {
                    found =true;
                    steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                    steam.setP(SuperH[i][0]);
                    steam.setV(SuperH[i][2]);
                    steam.setU(SuperH[i][3]);
                    steam.setH(SuperH[i][4]);
                    return steam;
                }
            }
            if (!found) {
                throw new NotDefinedException("TS");
            }
        }
        else {
            double [][] saturated = db.getSaturatedTableT();
            boolean found = false;
            boolean f = false;
            boolean g = false;
            int row = 0;
            double sf=0;
            for (int i = 0; i < saturated.length; i++) {
                if ( saturated[i][0] == T ) {
                    System.out.println("foundT");
                    System.out.println(T);
                    found =true;
                    row=i;
                    sf =saturated[i][10];
                    if (saturated[i][12] == s) {
                        found =true;
                        row=i;
                        g=true;
                        break;
                    }
                    if (saturated[i][10] == s) {
                        found =true;
                        row=i;
                        f=true;
                        break;
                    }
                }
            }
            if (!found) {
                return interpolatedSatT(T, s, "T", "S", saturated);
            }
            steam.setP(saturated[row][1]);
            if (g){
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                steam.setV(saturated[row][3]);
                steam.setU(saturated[row][6]);
                steam.setH(saturated[row][9]);
                return steam;
            }
            else if (f){
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                steam.setV(saturated[row][2]);
                steam.setU(saturated[row][4]);
                steam.setH(saturated[row][7]);
                return steam;
            }
            else if (saturated[row][10] < s && (saturated[row][12] > s)) {
                double X = (s-saturated[row][10])/(saturated[row][11]); //u = uf + X * ufg
                steam.setX(X);
                steam.setSteamPhase(SteamPhase.SaturatedMixture);
                double v =saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
                double u = saturated[row][4] + X*(saturated[row][5]);
                double h = saturated[row][7] + X*(saturated[row][8]);
                steam.setV(v);
                steam.setU(u);
                steam.setH(h);
                return steam;
            }
            else {
                double [][] table = sf < s? db.getSuperHeatedTable(): db.getCompressedLiquidTable();
                steam.setSteamPhase(sf < s? SteamPhase.SuperHeatedWater: SteamPhase.CompressedLiquid);
                for (int i = 0; i < table.length; i++) {
                    if (table[i][1] == T && table[i][5] == s) {
                        steam.setV(table[i][2]);
                        steam.setU(table[i][3]);
                        steam.setH(table[i][4]);
                        steam.setP(table[i][0]*1000);
                        return steam;
                    }
                }
                throw new NotDefinedException("TS");
            }
        }
        return steam;
    }

    public Steam findTheSteamUsingTPhase(double T, SteamPhase phase) {
        Steam steam = new Steam();
        steam.setT(T);
        steam.setSteamPhase(phase);
        return findTheSteamUsingTX(T,phase.getX());
    }

    // Find Steam Using Pressure and Volume
    public Steam findTheSteamUsingPV(double P, double v) { // need to be tested
        Steam steam = new Steam();
        steam.setP(P);
        steam.setV(v);
        boolean superHeated = false;
        boolean compressed = false;
        double [][] saturated = db.getSaturatedTableP();
        if (P > 22064){
            superHeated = true;
        }
        boolean found = false;
        boolean f = false;
        boolean g = false;
        boolean m = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if (saturated[i][0] == P) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found && !superHeated) {
            return interpolatedSatP(P, v, "P", "V", saturated);
        }
        if (saturated[row][2] == v) {
            f=true;
        }
        if (saturated[row][3] == v) {
            g=true;
        }
        if (saturated[row][2] < v && saturated[row][3] > v) {
            m=true;
        }
        if (saturated[row][3] < v) {
            superHeated=true;
        }
        if (saturated[row][2] > v) {
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
        else if (superHeated){
            P/=1000;
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] table = db.getSuperHeatedTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][2] == v) {
                        steam.setT(table[i][1]);
                        steam.setU(table[i][3]);
                        steam.setH(table[i][4]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                }
            }
            steam = interpolatedSuperHeatedOrCompressed(P,v,"P","V",table);
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            return steam;
        }
        else if (m){
            double X = (v-saturated[row][2])/(saturated[row][3]-saturated[row][2]) ; //u = uf + X * ufg
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
            double u =saturated[row][4] + X*saturated[row][5];
            double h = saturated[row][7] + X*(saturated[row][8]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setU(u);
            steam.setH(h);
            steam.setS(s);
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
                        steam.setU(saturated[i][4]);
                        steam.setH(saturated[i][7]);
                        steam.setS(saturated[i][10]);
                        return steam;
                    }
                }
                steam= interpolatedSatT(T,v,"T","V",saturated);
                steam.setP(P);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
            double [][] table = db.getCompressedLiquidTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][2] == v) {
                        steam.setT(table[i][1]);
                        steam.setU(table[i][3]);
                        steam.setH(table[i][4]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                }

            }
            steam = interpolatedSuperHeatedOrCompressed(P,v,"P","V",table);
            steam.setSteamPhase(SteamPhase.CompressedLiquid);
            return steam;
        }
        return steam;
    }

    // Find Steam Using Pressure and Internal Energy
    public Steam findTheSteamUsingPU(double P, double u) { // tested and working 100%
        Steam steam = new Steam();
        steam.setP(P);
        steam.setU(u);
        double [][] saturated = db.getSaturatedTableP();
        boolean superHeated = false;
        boolean compressed = false;
        if (P > 22064){
            superHeated = true;
        }
        boolean found = false;
        boolean f = false;
        boolean g = false;
        boolean m = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if (saturated[i][0] == P) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found && !superHeated) {
            for (int i = 0; i < saturated.length; i++) {
                if (i != saturated.length - 1 && saturated[i][0] < P && saturated[i + 1][0] > P) { // interpolation
                    found = true;
                    double t1 = saturated[i][1], t2 = saturated[i + 1][1];
                    double v1 = saturated[i][2], v2 = saturated[i + 1][2];
                    double h1 = saturated[i][7], h2 = saturated[i + 1][7];
                    double s1 = saturated[i][10], s2 = saturated[i + 1][10];

                    double interpolatedT = Interpolation.linear(saturated[i][0], t1, saturated[i + 1][0], t2, P);
                    double interpolatedV = Interpolation.linear(saturated[i][0], v1, saturated[i + 1][0], v2, P);
                    double interpolatedH = Interpolation.linear(saturated[i][0], h1, saturated[i + 1][0], h2, P);
                    double interpolatedS = Interpolation.linear(saturated[i][0], s1, saturated[i + 1][0], s2, P);

                    steam.setT(interpolatedT);
                    steam.setV(interpolatedV);
                    steam.setH(interpolatedH);
                    steam.setS(interpolatedS);
                    steam.setSteamPhase(SteamPhase.SaturatedMixture);
                    return steam;
                }
            }
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
        else if (superHeated){
            P/=1000;
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            found =false;
            double [][] table = db.getSuperHeatedTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    found=true;
                    if (table[i][3] == u) {
                        steam.setT(table[i][1]);
                        steam.setV(table[i][2]);
                        steam.setH(table[i][4]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                    break;
                }
            }
            if (found) {
                steam = interpolatedSuperHeatedOrCompressed(P,u,"P","U",table);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }
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
            return steam;
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
                        steam.setH(saturated[i][7]);
                        steam.setS(saturated[i][10]);
                        return steam;
                    }
                }
                 steam =interpolatedSatT(T,u,"T","U",saturated);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                steam.setP(P);
                return steam;
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
                }

            }
           steam = interpolatedSuperHeatedOrCompressed(P,u,"P","U",table);
            steam.setSteamPhase(SteamPhase.CompressedLiquid);
            return steam;
        }
        return steam;
    }

    // Find Steam Using Pressure and Enthalpy
    public Steam findTheSteamUsingPH(double P, double h) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setH(h);
        boolean superHeated = false;
        boolean compressed = false;
        double [][] saturated = db.getSaturatedTableP();
        if (P > 22064){
            superHeated = true;
        }
        boolean found = false;
        boolean f = false;
        boolean g = false;
        boolean m = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if (saturated[i][0] == P) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found && !superHeated) {
            return interpolatedSatP(P, h, "P", "H", saturated);
        }
        if (saturated[row][7] == h) {
            f=true;
        }
        if (saturated[row][9] == h) {
            g=true;
        }
        if (saturated[row][7] < h && saturated[row][9] > h) {
            m=true;
        }
        if (saturated[row][9] < h) {
            superHeated=true;
        }
        if (saturated[row][7] > h) {
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
        else if (superHeated){
            P/=1000;
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] table = db.getSuperHeatedTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][2] == h) {
                        steam.setT(table[i][1]);
                        steam.setV(table[i][2]);
                        steam.setU(table[i][3]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                }
            }
            steam = interpolatedSuperHeatedOrCompressed(P,h,"P","H",table);
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            return steam;
        }
        else if (m){
            double X = (h-saturated[row][7])/(saturated[row][8]) ; //u = uf + X * ufg
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
            double u =saturated[row][4] + X*saturated[row][5];
            double v = saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double s = saturated[row][10] + X*(saturated[row][11]);
            steam.setU(u);
            steam.setV(v);
            steam.setS(s);
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
                        steam.setU(saturated[i][4]);
                        steam.setV(saturated[i][2]);
                        steam.setS(saturated[i][10]);
                        return steam;
                    }
                }
                steam= interpolatedSatT(T,h,"T","H",saturated);
                steam.setP(P);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
            double [][] table = db.getCompressedLiquidTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][4] == h) {
                        steam.setT(table[i][1]);
                        steam.setU(table[i][3]);
                        steam.setV(table[i][2]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                }

            }
            steam = interpolatedSuperHeatedOrCompressed(P,h,"P","H",table);
            steam.setSteamPhase(SteamPhase.CompressedLiquid);
            return steam;
        }
        return steam;
    }

    public Steam findTheSteamUsingPPhase(double P, SteamPhase steamPhase) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setSteamPhase(steamPhase);
        return findTheSteamUsingPX(P, steamPhase.getX());
    }

    // Find Steam Using Pressure and Quality
    public Steam findTheSteamUsingPX(double P, double X) {
        Steam steam = new Steam();
        steam.setP(P);
        steam.setX(X);
        setPhase(steam, X);
        double [][] saturated = db.getSaturatedTableP();
        boolean found = false;
        int row=0;
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
            steam.setV(saturated[row][3]);
            steam.setU(saturated[row][6]);
            steam.setH(saturated[row][9]);
            steam.setS(saturated[row][12]);
        }
        else if (X == 0.0){
            steam.setSteamPhase(SteamPhase.SaturatedLiquid);
            steam.setT(saturated[row][1]);
            steam.setV(saturated[row][2]);
            steam.setU(saturated[row][4]);
            steam.setH(saturated[row][7]);
            steam.setS(saturated[row][10]);
        }
        else {
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
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
        boolean superHeated = false;
        boolean compressed = false;
        double [][] saturated = db.getSaturatedTableP();
        if (P > 22064){
            superHeated = true;
        }
        boolean found = false;
        boolean f = false;
        boolean g = false;
        boolean m = false;
        int row = 0;
        for (int i = 0; i < saturated.length; i++) {
            if (saturated[i][0] == P) {
                found =true;
                row=i;
                break;
            }
        }
        if (!found && !superHeated) {
            return interpolatedSatP(P, s, "P", "S", saturated);
        }
        if (saturated[row][10] == s) {
            f=true;
        }
        if (saturated[row][12] == s) {
            g=true;
        }
        if (saturated[row][10] < s && saturated[row][12] > s) {
            m=true;
        }
        if (saturated[row][12] < s) {
            superHeated=true;
        }
        if (saturated[row][10] > s) {
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
        else if (superHeated){
            P/=1000;
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            double [][] table = db.getSuperHeatedTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][5] == s) {
                        steam.setT(table[i][1]);
                        steam.setV(table[i][2]);
                        steam.setU(table[i][3]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                }
            }
            steam = interpolatedSuperHeatedOrCompressed(P,s,"P","S",table);
            steam.setSteamPhase(SteamPhase.SuperHeatedWater);
            return steam;
        }
        else if (m){
            double X = (s-saturated[row][10])/(saturated[row][11]) ; //u = uf + X * ufg
            steam.setSteamPhase(SteamPhase.SaturatedMixture);
            steam.setX(X);
            double u =saturated[row][4] + X*saturated[row][5];
            double v = saturated[row][2] + X*(saturated[row][3]-saturated[row][2]);
            double h = saturated[row][7] + X*(saturated[row][8]);
            steam.setU(u);
            steam.setV(v);
            steam.setH(h);
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
                        steam.setU(saturated[i][4]);
                        steam.setV(saturated[i][2]);
                        return steam;
                    }
                }
                steam= interpolatedSatT(T,s,"T","S",saturated);
                steam.setP(P);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
            double [][] table = db.getCompressedLiquidTable();
            for (int i = 0; i < table.length; i++) {
                if (P == table[i][0]) {
                    if (table[i][5] == s) {
                        steam.setT(table[i][1]);
                        steam.setU(table[i][3]);
                        steam.setV(table[i][2]);
                        steam.setS(table[i][5]);
                        return steam;
                    }
                }
            }
            steam = interpolatedSuperHeatedOrCompressed(P,s,"P","S",table);
            steam.setSteamPhase(SteamPhase.CompressedLiquid);
            return steam;
        }
        return steam;
    }

    public Steam findTheSteamUsingUV(double u, double v) {
        Steam steam = new Steam();
        steam.setU(u);
        steam.setV(v);
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

    public Steam findTheSuperHeatedSteamOrCompressedLiquid(double x, double y, int i, int j, SteamPhase phase) { //all works
        Steam steam = new Steam();
        steam.setSteamPhase(phase);
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
        for (int k=0 ;k<table.length;k++) { //find before interpolation
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

    private Steam interpolatedSatT(double v1, double v2, String s1, String s2, double [][] saturated) throws CannotBeInterpolated {
        Steam steam = new Steam();
        int index1 = 0;
        switch (s1) {
            case "T":
                index1 = 0;
                break;
            case "P":
                index1 = 1;
                break;
            case "V":
                index1 = 2;
                break;
            case "U":
                index1 = 4;
                break;
            case "H":
                index1 = 7;
                break;
            case "S":
                index1 = 10;
                break;
            default:
                throw new CannotBeInterpolated(s1);
        }
        for (int i = 0; i < saturated.length - 1; i++) {
            if (saturated[i][index1] < v1 && saturated[i + 1][index1] > v1) { // Interpolation
                double interpolatedP = 0, interpolatedT = 0, interpolatedV = 0, interpolatedU = 0, interpolatedH = 0, interpolatedS = 0;
                if (!"T".equals(s1) && !"T".equals(s2)) {
                    interpolatedT = Interpolation.linear(saturated[i][index1], saturated[i][0], saturated[i + 1][index1], saturated[i + 1][0], v1);
                } else {
                    if ("T".equals(s1)) {
                        interpolatedT = v1;
                    } else {
                        interpolatedT = v2;
                    }
                }
                if (!"P".equals(s1) && !"P".equals(s2)) {
                    interpolatedP = Interpolation.linear(saturated[i][index1], saturated[i][1], saturated[i + 1][index1], saturated[i + 1][1], v1);
                } else {
                    if ("P".equals(s1)) {
                        interpolatedP = v1;
                    } else {
                        interpolatedP = v2;
                    }
                }
                if (!"V".equals(s1) && !"V".equals(s2)) {
                    interpolatedV = Interpolation.linear(saturated[i][index1], saturated[i][2], saturated[i + 1][index1], saturated[i + 1][2], v1);
                } else {
                    if ("V".equals(s1)) {
                        interpolatedV = v1;
                    } else {
                        interpolatedV = v2;
                    }
                }
                if (!"U".equals(s1) && !"U".equals(s2)) {
                    interpolatedU = Interpolation.linear(saturated[i][index1], saturated[i][4], saturated[i + 1][index1], saturated[i + 1][4], v1);
                } else {
                    if ("U".equals(s1)) {
                        interpolatedU = v1;
                    } else {
                        interpolatedU = v2;
                    }
                }
                if (!"H".equals(s1) && !"H".equals(s2)) {
                    interpolatedH = Interpolation.linear(saturated[i][index1], saturated[i][7], saturated[i + 1][index1], saturated[i + 1][7], v1);
                } else {
                    if ("H".equals(s1)) {
                        interpolatedH = v1;
                    } else {
                        interpolatedH = v2;
                    }
                }
                if (!"S".equals(s1) && !"S".equals(s2)) {
                    interpolatedS = Interpolation.linear(saturated[i][index1], saturated[i][10], saturated[i + 1][index1], saturated[i + 1][10], v1);
                } else {
                    if ("S".equals(s1)) {
                        interpolatedS = v1;
                    } else {
                        interpolatedS = v2;
                    }
                }
                steam.setU(interpolatedU);
                steam.setV(interpolatedV);
                steam.setH(interpolatedH);
                steam.setS(interpolatedS);
                steam.setP(interpolatedP);
                steam.setT(interpolatedT);
                return steam;
            }
        }
        throw new CannotBeInterpolated("interpolatedSatT");
    }
    private Steam interpolatedSatP(double v1, double v2, String s1, String s2, double [][] saturated) throws CannotBeInterpolated {
        Steam steam = new Steam();
        int index1 = 0;
        switch (s1) {
            case "T":
                index1 = 1;
                break;
            case "P":
                index1 = 0;
                break;
            case "V":
                index1 = 2;
                break;
            case "U":
                index1 = 4;
                break;
            case "H":
                index1 = 7;
                break;
            case "S":
                index1 = 10;
                break;
            default:
                throw new CannotBeInterpolated(s1);
        }
        for (int i = 0; i < saturated.length - 1; i++) {
            if (saturated[i][index1] < v1 && saturated[i + 1][index1] > v1) { // Interpolation
                double interpolatedP = 0, interpolatedT = 0, interpolatedV = 0, interpolatedU = 0, interpolatedH = 0, interpolatedS = 0;
                if (!"T".equals(s1) && !"T".equals(s2)) {
                    interpolatedT = Interpolation.linear(saturated[i][index1], saturated[i][1], saturated[i + 1][index1], saturated[i + 1][1], v1);
                } else {
                    if ("T".equals(s1)) {
                        interpolatedT = v1;
                    } else {
                        interpolatedT = v2;
                    }
                }
                if (!"P".equals(s1) && !"P".equals(s2)) {
                    interpolatedP = Interpolation.linear(saturated[i][index1], saturated[i][0], saturated[i + 1][index1], saturated[i + 1][0], v1);
                } else {
                    if ("P".equals(s1)) {
                        interpolatedP = v1;
                    } else {
                        interpolatedP = v2;
                    }
                }
                if (!"V".equals(s1) && !"V".equals(s2)) {
                    interpolatedV = Interpolation.linear(saturated[i][index1], saturated[i][2], saturated[i + 1][index1], saturated[i + 1][2], v1);
                } else {
                    if ("V".equals(s1)) {
                        interpolatedV = v1;
                    } else {
                        interpolatedV = v2;
                    }
                }
                if (!"U".equals(s1) && !"U".equals(s2)) {
                    interpolatedU = Interpolation.linear(saturated[i][index1], saturated[i][4], saturated[i + 1][index1], saturated[i + 1][4], v1);
                } else {
                    if ("U".equals(s1)) {
                        interpolatedU = v1;
                    } else {
                        interpolatedU = v2;
                    }
                }
                if (!"H".equals(s1) && !"H".equals(s2)) {
                    interpolatedH = Interpolation.linear(saturated[i][index1], saturated[i][7], saturated[i + 1][index1], saturated[i + 1][7], v1);
                } else {
                    if ("H".equals(s1)) {
                        interpolatedH = v1;
                    } else {
                        interpolatedH = v2;
                    }
                }
                if (!"S".equals(s1) && !"S".equals(s2)) {
                    interpolatedS = Interpolation.linear(saturated[i][index1], saturated[i][10], saturated[i + 1][index1], saturated[i + 1][10], v1);
                } else {
                    if ("S".equals(s1)) {
                        interpolatedS = v1;
                    } else {
                        interpolatedS = v2;
                    }
                }
                steam.setU(interpolatedU);
                steam.setV(interpolatedV);
                steam.setH(interpolatedH);
                steam.setS(interpolatedS);
                steam.setP(interpolatedP);
                steam.setT(interpolatedT);
                return steam;
            }
        }
        throw new CannotBeInterpolated("interpolatedSatT");
    }

    private Steam interpolatedSuperHeatedOrCompressed(double v1, double v2, String s1, String s2, double [][] saturated)
            throws CannotBeInterpolated {
        Steam steam = new Steam();
        int index = switch (s2) {
            case "T" -> 1;
            case "V" -> 2;
            case "U" -> 3;
            case "H" -> 4;
            case "S" -> 5;
            default -> throw new CannotBeInterpolated(s1);
        };
        for (int i = 0; i < saturated.length - 1; i++) {
            if (saturated[i][0]==v1) {
                if (saturated[i][index] < v2 && saturated[i + 1][index] > v2) { // Interpolation
                    double interpolatedT = 0, interpolatedV = 0, interpolatedU = 0, interpolatedH = 0, interpolatedS = 0;
                    if (!"T".equals(s1) && !"T".equals(s2)) {
                        interpolatedT = Interpolation.linear(saturated[i][index], saturated[i][1], saturated[i + 1][index], saturated[i + 1][1], v2);
                    } else {
                        if ("T".equals(s1)) {
                            interpolatedT = v1;
                        } else {
                            interpolatedT = v2;
                        }
                    }
                    if (!"V".equals(s1) && !"V".equals(s2)) {
                        interpolatedV = Interpolation.linear(saturated[i][index], saturated[i][2], saturated[i + 1][index], saturated[i + 1][2], v2);
                    } else {
                        if ("V".equals(s1)) {
                            interpolatedV = v1;
                        } else {
                            interpolatedV = v2;
                        }
                    }
                    if (!"U".equals(s1) && !"U".equals(s2)) {
                        interpolatedU = Interpolation.linear(saturated[i][index], saturated[i][3], saturated[i + 1][index], saturated[i + 1][3], v2);
                    } else {
                        if ("U".equals(s1)) {
                            interpolatedU = v1;
                        } else {
                            interpolatedU = v2;
                        }
                    }
                    if (!"H".equals(s1) && !"H".equals(s2)) {
                        interpolatedH = Interpolation.linear(saturated[i][index], saturated[i][4], saturated[i + 1][index], saturated[i + 1][4], v2);
                    } else {
                        if ("H".equals(s1)) {
                            interpolatedH = v1;
                        } else {
                            interpolatedH = v2;
                        }
                    }
                    if (!"S".equals(s1) && !"S".equals(s2)) {
                        interpolatedS = Interpolation.linear(saturated[i][index], saturated[i][5], saturated[i + 1][index], saturated[i + 1][5], v2);
                    } else {
                        if ("S".equals(s1)) {
                            interpolatedS = v1;
                        } else {
                            interpolatedS = v2;
                        }
                    }
                    steam.setU(interpolatedU);
                    steam.setV(interpolatedV);
                    steam.setH(interpolatedH);
                    steam.setS(interpolatedS);
                    steam.setP(v1);
                    steam.setT(interpolatedT);
                    return steam;
                }
            }
        }
        throw new CannotBeInterpolated("interpolatedCompOrSuper");
    }

    public static void main(String args []){
        Controller controller = new Controller();
        Steam steam = controller.findTheSteamUsingTH(180, 700);
        System.out.println(steam.toString());
    }

}

