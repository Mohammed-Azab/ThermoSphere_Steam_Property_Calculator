package steamTables;

public class Water extends Fluid{

    public Water() {
        name = "Water";
        molarMass = 18.015; // g/mol
        criticalPressure = 22.06; // MPa
        criticalTemperature = 647.1; // Kelvin
        criticalVolume = 0.0560; // m3/kmol

        // Commonly used values at standard conditions (e.g., 25°C or 298.15 K, 1 atm or 0.101325 MPa)
        density = 997; // kg/m3 (approximate at room temperature)
        specificHeat = 4.18; // kJ/(kg·K)
        molarVolume = molarMass / density; // m3/kmol
        specificVolume = 1 / density; // m3/kg
        viscosity = 0.001; // Pa·s (dynamic viscosity at room temperature)
        thermalConductivity = 0.6; // W/(m·K)
        thermalExpansion = 0.00021; // 1/K (approximate for liquid water)
    }

}
