package steamTables;

public class EquationOfState {

    /**
     * Calculates the pressure using the Ideal Gas Law: P = (nRT) / V
     *
     * @param n  Number of moles (mol)
     * @param R  Ideal gas constant (J/mol·K)
     * @param T  Temperature (K)
     * @param V  Volume (m³)
     * @return Pressure (Pa)
     */
    public static double idealGasLaw(double n, double R, double T, double V) {
        if (V <= 0) {
            throw new IllegalArgumentException("Volume must be greater than zero.");
        }
        return (n * R * T) / V;
    }

    /**
     * Calculates the pressure using the Van der Waals equation:
     * P = (nRT) / (V - nb) - (a * n²) / V²
     *
     * @param n  Number of moles (mol)
     * @param R  Ideal gas constant (J/mol·K)
     * @param T  Temperature (K)
     * @param V  Volume (m³)
     * @param a  Attraction parameter (Pa·m³²/mol²)
     * @param b  Repulsion parameter (m³/mol)
     * @return Pressure (Pa)
     */
    public static double vanDerWaals(double n, double R, double T, double V, double a, double b) {
        if (V <= n * b) {
            throw new IllegalArgumentException("Volume must be greater than nb for the van der Waals equation.");
        }
        double firstTerm = (n * R * T) / (V - n * b);
        double secondTerm = (a * Math.pow(n, 2)) / Math.pow(V, 2);
        return firstTerm - secondTerm;
    }

    /**
     * Calculates the molar volume using the Ideal Gas Law: Vm = (RT) / P
     *
     * @param R  Ideal gas constant (J/mol·K)
     * @param T  Temperature (K)
     * @param P  Pressure (Pa)
     * @return Molar Volume (m³/mol)
     */
    public static double calculateMolarVolume(double R, double T, double P) {
        if (P <= 0) {
            throw new IllegalArgumentException("Pressure must be greater than zero.");
        }
        return (R * T) / P;
    }

    /**
     * General function to calculate density: ρ = nM / V
     *
     * @param n  Number of moles (mol)
     * @param M  Molar mass (kg/mol)
     * @param V  Volume (m³)
     * @return Density (kg/m³)
     */
    public static double calculateDensity(double n, double M, double V) {
        if (V <= 0) {
            throw new IllegalArgumentException("Volume must be greater than zero.");
        }
        return (n * M) / V;
    }
}
