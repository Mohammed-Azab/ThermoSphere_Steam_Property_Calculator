package steamTables;


import Exceptions.CannotBeInterpolated;
import Exceptions.MoreInfoNeeded;
import Exceptions.NotDefinedException;

import java.security.spec.ECField;

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
                if (vf > v) { //compressed
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
                if (uf > u) { //compressed
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
                double [][] table = hf < h? db.getSuperHeatedTable(): db.getCompressedLiquidTable();
                steam.setSteamPhase(hf < h? SteamPhase.SuperHeatedWater: SteamPhase.CompressedLiquid);
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
            return interpolatedSatT(T, X, "T", "X", saturated);
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
            return interpolatedSatP(P, u, "P", "U", saturated);
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
            return interpolatedSatP(P, X, "P", "X", saturated);
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
        double [][] table = db.getSaturatedTableT();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][4] && v == table[i][2]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setH(table[i][7]);
                steam.setS(table[i][10]);
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                return steam;
            }
            else if (u == table[i][6] && v == table[i][3]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setH(table[i][9]);
                steam.setS(table[i][12]);
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                return steam;
            }
        }
        table = db.getCompressedLiquidTable();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][3] && v == table[i][2]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setH(table[i][4]);
                steam.setS(table[i][5]);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
        }
        table = db.getSuperHeatedTable();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][3] && v == table[i][2]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setH(table[i][4]);
                steam.setS(table[i][5]);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }
        }
        throw new NotDefinedException();
    }

    public Steam findTheSteamUsingUS(double u, double s) {
        Steam steam = new Steam();
        steam.setU(u);
        steam.setS(s);
        double [][] table = db.getSaturatedTableT();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][4] && s == table[i][10]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setH(table[i][7]);
                steam.setV(table[i][2]);
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                return steam;
            }
            else if (u == table[i][6] && s == table[i][12]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setH(table[i][9]);
                steam.setV(table[i][3]);
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                return steam;
            }
        }
        table = db.getCompressedLiquidTable();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][3] && s == table[i][5]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setH(table[i][4]);
                steam.setV(table[i][2]);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
        }
        table = db.getSuperHeatedTable();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][3] && s == table[i][5]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setH(table[i][4]);
                steam.setV(table[i][2]);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }
        }
        throw new NotDefinedException();
    }

    public Steam findTheSteamUsingUH(double u, double h) {
        Steam steam = new Steam();
        steam.setU(u);
        steam.setH(h);
        double [][] table = db.getSaturatedTableT();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][4] && h == table[i][7]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setS(table[i][10]);
                steam.setV(table[i][2]);
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                return steam;
            }
            else if (u == table[i][6] && h == table[i][9]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setS(table[i][12]);
                steam.setV(table[i][3]);
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                return steam;
            }
        }
        table = db.getCompressedLiquidTable();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][3] && h == table[i][4]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setS(table[i][5]);
                steam.setV(table[i][2]);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
        }
        table = db.getSuperHeatedTable();
        for (int i = 0; i < table.length; i++) {
            if (u == table[i][3] && h == table[i][4]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setS(table[i][5]);
                steam.setV(table[i][2]);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }
        }
        throw new NotDefinedException();
    }

    public Steam findTheSteamUsingUX(double u, double X) {
        return findWithX(u,X,4);
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

    public Steam findTheSteamUsingHPhase(double H, SteamPhase phase) {
        return findTheSteamUsingHX(H, phase.getX());
    }

    public Steam findTheSteamUsingSPhase(double S, SteamPhase phase) {
        return findTheSteamUsingSX(S, phase.getX());
    }

    public Steam findTheSteamUsingHX(double H, double X) {
        return findWithX(H,X,7);
    }

    public Steam findTheSteamUsingXS(double X, double s) {
        return findTheSteamUsingSX(s,X);
    }

    public Steam findTheSteamUsingVX(double v, double X) {
        return findWithX(v,X,2);
    }

    public Steam findTheSteamUsingVS(double v, double s) {
        Steam steam = new Steam();
        steam.setS(s);
        steam.setV(v);
        double [][] table = db.getSaturatedTableT();
        for (int i = 0; i < table.length; i++) {
            if (v == table[i][2] && s == table[i][10]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setH(table[i][7]);
                steam.setU(table[i][4]);
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                return steam;
            }
            else if (v == table[i][3] && s == table[i][12]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setH(table[i][9]);
                steam.setU(table[i][6]);
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                return steam;
            }
        }
        table = db.getCompressedLiquidTable();
        for (int i = 0; i < table.length; i++) {
            if (v == table[i][2] && s == table[i][5]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setH(table[i][4]);
                steam.setU(table[i][3]);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
        }
        table = db.getSuperHeatedTable();
        for (int i = 0; i < table.length; i++) {
            if (v == table[i][2] && s == table[i][5]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setH(table[i][4]);
                steam.setU(table[i][3]);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }
        }
        throw new NotDefinedException();
    }

    public Steam findTheSteamUsingVH(double v, double h) {
        Steam steam = new Steam();
        steam.setH(h);
        steam.setV(v);
        double [][] table = db.getSaturatedTableT();
        for (int i = 0; i < table.length; i++) {
            if (v == table[i][2] && h == table[i][7]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setS(table[i][10]);
                steam.setU(table[i][4]);
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                return steam;
            }
            else if (v == table[i][3] && h == table[i][9]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setS(table[i][12]);
                steam.setU(table[i][6]);
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                return steam;
            }
        }
        table = db.getCompressedLiquidTable();
        for (int i = 0; i < table.length; i++) {
            if (v == table[i][2] && h == table[i][4]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setS(table[i][5]);
                steam.setU(table[i][3]);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
        }
        table = db.getSuperHeatedTable();
        for (int i = 0; i < table.length; i++) {
            if (v == table[i][2] && h == table[i][4]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setS(table[i][5]);
                steam.setU(table[i][3]);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }
        }
        throw new NotDefinedException();
    }

    public Steam findTheSteamUsingHS(double h, double s) {
        Steam steam = new Steam();
        steam.setH(h);
        steam.setS(s);
        double [][] table = db.getSaturatedTableT();
        for (int i = 0; i < table.length; i++) {
            if (s == table[i][10] && h == table[i][7]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setV(table[i][2]);
                steam.setU(table[i][4]);
                steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                return steam;
            }
            else if (s == table[i][12] && h == table[i][9]) {
                steam.setT(table[i][0]);
                steam.setP(table[i][1]);
                steam.setV(table[i][3]);
                steam.setU(table[i][6]);
                steam.setSteamPhase(SteamPhase.SaturatedVapour);
                return steam;
            }
        }
        table = db.getCompressedLiquidTable();
        for (int i = 0; i < table.length; i++) {
            if (s == table[i][5] && h == table[i][4]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setV(table[i][2]);
                steam.setU(table[i][3]);
                steam.setSteamPhase(SteamPhase.CompressedLiquid);
                return steam;
            }
        }
        table = db.getSuperHeatedTable();
        for (int i = 0; i < table.length; i++) {
            if (s == table[i][5] && h == table[i][4]) {
                steam.setT(table[i][1]);
                steam.setP(table[i][0]*1000);
                steam.setV(table[i][2]);
                steam.setU(table[i][3]);
                steam.setSteamPhase(SteamPhase.SuperHeatedWater);
                return steam;
            }
        }
        throw new NotDefinedException();
    }





    public Steam findTheSteamUsingSX(double S, double X) {
        return findWithX(S,X,10);
    }

    public DataBase getDb() {
        return db;
    }


    public Steam findTheSteamUsingUPhase(double u, SteamPhase phase) {
        return findTheSteamUsingUX(u,phase.getX());
    }
    public Steam findTheSteamUsingVPhase(double v, SteamPhase phase) {
        return findTheSteamUsingVX(v,phase.getX());
    }

    public Steam findTheSuperHeatedSteamOrCompressedLiquid(double x, double y, int i, int j, SteamPhase phase) { //all works
        Steam steam = new Steam();
        steam.setSteamPhase(phase);
        double [][] table = db.getSuperHeatedTable();
        if (phase == SteamPhase.CompressedLiquid){
            table = db.getCompressedLiquidTable();
            if (i==0){
                if (x<5) {
                    boolean found = false;
                    double T2=0;
                    double saturated[][] = db.getSaturatedTableP();
                    steam.setSteamPhase(SteamPhase.CompressedLiquid);
                    for (int k = 0; k < saturated.length; k++) {
                        if (saturated[k][0] == x) {
                            found = true;
                            T2 = saturated[i][1];
                            break;
                        }
                    }
                    if (!found) {
                        for (int k = 0; k < saturated.length - 1; k++) {
                            if (saturated[k][0] < x && saturated[k + 1][0] > x) { // Interpolation
                                double interpolatedT = Interpolation.linear(saturated[k][0], saturated[k][1],
                                        saturated[k + 1][0], saturated[k + 1][1], x);
                                double interpolatedU = Interpolation.linear(saturated[k][0], saturated[k][4],
                                        saturated[k + 1][0], saturated[k + 1][4], x);
                                double interpolatedV = Interpolation.linear(saturated[k][0], saturated[k][2],
                                        saturated[k + 1][0], saturated[k + 1][2], x);
                                double interpolatedH = Interpolation.linear(saturated[k][0], saturated[k][7],
                                        saturated[k + 1][0], saturated[k + 1][7], x);
                                double interpolatedS = Interpolation.linear(saturated[k][0], saturated[k][10],
                                        saturated[k + 1][0], saturated[k + 1][10], x);
                                steam.setU(interpolatedU);
                                steam.setV(interpolatedV);
                                steam.setH(interpolatedH);
                                steam.setS(interpolatedS);
                                steam.setP(x);
                                steam.setT(interpolatedT);
                                return steam;
                            }
                        }
                        throw new NotDefinedException("Pressure value not found for interpolation.");
                    }
                    saturated = db.getSaturatedTableT();
                    for (double[] doubles : saturated) {
                        if (doubles[0] == T2) {
                            steam.setP(x);
                            steam.setT(T2);
                            steam.setV(doubles[2]);
                            steam.setU(doubles[4]);
                            steam.setH(doubles[7]);
                            steam.setS(doubles[10]);
                            return steam;
                        }
                    }

                    String string = switch (j) {
                        case 1 -> "T";
                        case 2 -> "V";
                        case 3 -> "U";
                        case 4 -> "H";
                        case 5 -> "S";
                        default -> "X";
                    };
                    steam = interpolatedSatT(x,y,"P",string,saturated);
                    steam.setSteamPhase(SteamPhase.CompressedLiquid);
                    return steam;
                }
            }
            if (j==0){
                if (x<5) {
                    int row =0;
                    boolean found = false;
                    double T2=0;
                    double saturated[][] = db.getSaturatedTableP();
                    steam.setSteamPhase(SteamPhase.CompressedLiquid);
                    for (int k = 0; k < saturated.length; k++) {
                        if (saturated[k][0] == y) {
                            found = true;
                            row = i;
                            T2 = saturated[i][1];
                            break;
                        }
                    }
                    if (!found) {
                        for (int k = 0; k < saturated.length - 1; k++) {
                            if (saturated[k][0] < y && saturated[k + 1][0] > y) { // Interpolation
                                double interpolatedT = Interpolation.linear(saturated[k][0], saturated[k][1],
                                        saturated[k + 1][0], saturated[k + 1][1], y);
                                double interpolatedU = Interpolation.linear(saturated[k][0], saturated[k][4],
                                        saturated[k + 1][0], saturated[k + 1][4], y);
                                double interpolatedV = Interpolation.linear(saturated[k][0], saturated[k][2],
                                        saturated[k + 1][0], saturated[k + 1][2], y);
                                double interpolatedH = Interpolation.linear(saturated[k][0], saturated[k][7],
                                        saturated[k + 1][0], saturated[k + 1][7], y);
                                double interpolatedS = Interpolation.linear(saturated[k][0], saturated[k][10],
                                        saturated[k + 1][0], saturated[k + 1][10], y);
                                steam.setU(interpolatedU);
                                steam.setV(interpolatedV);
                                steam.setH(interpolatedH);
                                steam.setS(interpolatedS);
                                steam.setP(y);
                                steam.setT(interpolatedT);
                                return steam;
                            }
                        }
                        throw new NotDefinedException("Pressure value not found for interpolation.");
                    }
                    saturated = db.getSaturatedTableT();
                    for (double[] doubles : saturated) {
                        if (doubles[0] == T2) {
                            steam.setP(y);
                            steam.setT(T2);
                            steam.setV(doubles[2]);
                            steam.setU(doubles[4]);
                            steam.setH(doubles[7]);
                            steam.setS(doubles[10]);
                            return steam;
                        }
                    }

                    String string = switch (i) {
                        case 1 -> "T";
                        case 2 -> "V";
                        case 3 -> "U";
                        case 4 -> "H";
                        case 5 -> "S";
                        default -> "X";
                    };
                    steam = interpolatedSatT(x,y,string,"P",saturated);
                    steam.setSteamPhase(SteamPhase.CompressedLiquid);
                    return steam;
                }
            }
        }

        for (double[] doubles : table) { //find before interpolation
            if (doubles[i] == x && doubles[j] == y) {
                steam.setP(doubles[0]);
                steam.setT(doubles[1]);
                steam.setV(doubles[2]);
                steam.setU(doubles[3]);
                steam.setH(doubles[4]);
                steam.setS(doubles[5]);
                return steam;
            }
        }
        String str1 = switch (i) {
            case 0 -> "P";
            case 1 -> "T";
            case 2 -> "V";
            case 3 -> "U";
            case 4 -> "H";
            case 5 -> "S";
            default -> "X";
        };
        String str2 = switch (j) {
            case 0 -> "P";
            case 1 -> "T";
            case 2 -> "V";
            case 3 -> "U";
            case 4 -> "H";
            case 5 -> "S";
            default -> "X";
        };
        String str = str1.equals("P")? str2 : str1;
        double z= j==0? y : x;
        double m = z==x? y : x;
        try {
            steam = interpolatedSuperHeatedOrCompressed(z,m,"P",str,table);
            steam.setSteamPhase(phase);
            return steam;
        }
        catch (Exception e) {
            throw new NotDefinedException(phase +" is not defined");
        }

    }

    private Steam interpolatedSatT(double v1, double v2, String s1, String s2, double [][] saturated) throws CannotBeInterpolated {
        Steam steam = new Steam();
        int index1 = 0, index2=0, index3=0;
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
        switch (s2) {
            case "T":
                index2 = 0;
                break;
            case "P":
                index2 = 1;
                break;
            case "V":
                index2 = 2; index3=3;
                break;
            case "U":
                index2 = 4; index3=6;
                break;
            case "H":
                index2 = 7; index3=9;
                break;
            case "X":
            case "S":
                index2 = 10; index3=12;
                break;
            default:
                throw new CannotBeInterpolated(s1);
        }
        for (int i = 0; i < saturated.length - 1; i++) {
            if (saturated[i][index1] < v1 && saturated[i + 1][index1] > v1) { // Interpolation
                double interpolatedP = 0, interpolatedT = 0, interpolatedV = 0, interpolatedU = 0, interpolatedH = 0, interpolatedS = 0;
                double Valf= Interpolation.linear(saturated[i][index1], saturated[i][index2], saturated[i + 1][index1], saturated[i + 1][index2], v1);
                double Valg= Interpolation.linear(saturated[i][index1], saturated[i][index3], saturated[i + 1][index1], saturated[i + 1][index3], v1);
                double x = s2.equals("X")? v2 : (v2 - Valf)/ (Valg - Valf);
                steam.setX(x);
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
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][2], saturated[i + 1][index1], saturated[i + 1][2], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][3], saturated[i + 1][index1], saturated[i + 1][3], v1);
                    interpolatedV = Qf + x * (Qg-Qf) ;
                } else {
                    if ("V".equals(s1)) {
                        interpolatedV = v1;
                    } else {
                        interpolatedV = v2;
                    }
                }
                if (!"U".equals(s1) && !"U".equals(s2)) {
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][4], saturated[i + 1][index1], saturated[i + 1][4], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][6], saturated[i + 1][index1], saturated[i + 1][6], v1);
                    interpolatedU = Qf + x * (Qg-Qf) ;
                } else {
                    if ("U".equals(s1)) {
                        interpolatedU = v1;
                    } else {
                        interpolatedU = v2;
                    }
                }
                if (!"H".equals(s1) && !"H".equals(s2)) {
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][7], saturated[i + 1][index1], saturated[i + 1][7], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][9], saturated[i + 1][index1], saturated[i + 1][9], v1);
                    interpolatedH = Qf + x * (Qg-Qf) ;
                } else {
                    if ("H".equals(s1)) {
                        interpolatedH = v1;
                    } else {
                        interpolatedH = v2;
                    }
                }
                if (!"S".equals(s1) && !"S".equals(s2)) {
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][10], saturated[i + 1][index1], saturated[i + 1][10], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][12], saturated[i + 1][index1], saturated[i + 1][12], v1);
                    interpolatedS = Qf + x * (Qg-Qf) ;
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
        int index1 = 0, index2=0, index3=0;
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
        switch (s2) {
            case "T":
                index2 = 1;
                break;
            case "P":
                index2 = 0;
                break;
            case "V":
                index2 = 2; index3=3;
                break;
            case "U":
                index2 = 4; index3=6;
                break;
            case "H":
                index2 = 7; index3=9;
                break;
            case "X":
            case "S":
                index2 = 10; index3=12;
                break;
            default:
                throw new CannotBeInterpolated(s1);
        }
        for (int i = 0; i < saturated.length - 1; i++) {
            if (saturated[i][index1] < v1 && saturated[i + 1][index1] > v1) { // Interpolation
                double Valf= Interpolation.linear(saturated[i][index1], saturated[i][index2], saturated[i + 1][index1], saturated[i + 1][index2], v1);
                double Valg= Interpolation.linear(saturated[i][index1], saturated[i][index3], saturated[i + 1][index1], saturated[i + 1][index3], v1);
                double x = s2.equals("X")? v2 : (v2 - Valf)/ (Valg - Valf);
                steam.setX(x);
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
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][2], saturated[i + 1][index1], saturated[i + 1][2], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][3], saturated[i + 1][index1], saturated[i + 1][3], v1);
                    interpolatedV = Qf + x * (Qg-Qf) ;
                } else {
                    if ("V".equals(s1)) {
                        interpolatedV = v1;
                    } else {
                        interpolatedV = v2;
                    }
                }
                if (!"U".equals(s1) && !"U".equals(s2)) {
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][4], saturated[i + 1][index1], saturated[i + 1][4], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][6], saturated[i + 1][index1], saturated[i + 1][6], v1);
                    interpolatedU = Qf + x * (Qg-Qf) ;
                } else {
                    if ("U".equals(s1)) {
                        interpolatedU = v1;
                    } else {
                        interpolatedU = v2;
                    }
                }
                if (!"H".equals(s1) && !"H".equals(s2)) {
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][7], saturated[i + 1][index1], saturated[i + 1][7], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][9], saturated[i + 1][index1], saturated[i + 1][9], v1);
                    interpolatedH = Qf + x * (Qg-Qf) ;
                } else {
                    if ("H".equals(s1)) {
                        interpolatedH = v1;
                    } else {
                        interpolatedH = v2;
                    }
                }
                if (!"S".equals(s1) && !"S".equals(s2)) {
                    double Qf= Interpolation.linear(saturated[i][index1], saturated[i][10], saturated[i + 1][index1], saturated[i + 1][10], v1);
                    double Qg= Interpolation.linear(saturated[i][index1], saturated[i][12], saturated[i + 1][index1], saturated[i + 1][12], v1);
                    interpolatedS = Qf + x * (Qg-Qf) ;
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

    private Steam findWithX(double v1, double x, int index) throws MoreInfoNeeded {
        Steam steam = new Steam();
        steam.setX(x);
        double [] [] saturated = db.getSaturatedTableT();
        if (x == 1.0){
            index = index == 2 ? index - 1 : index;
            for (int i=0;i<saturated.length;i++){
                if (saturated[i][index+2]==v1){
                    steam.setP(saturated[i][0]);
                    steam.setT(saturated[i][1]);
                    steam.setV(saturated[i][3]);
                    steam.setU(saturated[i][6]);
                    steam.setH(saturated[i][9]);
                    steam.setS(saturated[i][12]);
                    steam.setSteamPhase(SteamPhase.SaturatedVapour);
                    return steam;
                }
            }
        }
        else if (x == 0.0){
            for (int i=0;i<saturated.length;i++){
                if (saturated[i][index]==v1){
                    steam.setP(saturated[i][0]);
                    steam.setT(saturated[i][1]);
                    steam.setV(saturated[i][2]);
                    steam.setU(saturated[i][4]);
                    steam.setH(saturated[i][7]);
                    steam.setS(saturated[i][10]);
                    steam.setSteamPhase(SteamPhase.SaturatedLiquid);
                    return steam;
                }
            }
        }
        else {
            throw new MoreInfoNeeded();
        }
        return steam;
    }

    public static void main(String args []){
        Controller controller = new Controller();
        Steam steam = controller.findTheSteamUsingTH(180, 700);
        System.out.println(steam.toString());
    }

}

