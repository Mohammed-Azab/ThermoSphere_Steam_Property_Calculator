package steamTables;

public abstract class Fluid {
    protected String name;
    protected double density;
    protected double viscosity;
    protected double specificHeat;
    protected double molarMass;
    protected double molarVolume;
    protected double specificVolume;
    protected double specificHeatCapacity;
    protected double thermalConductivity;
    protected double thermalExpansion;
    protected double criticalPressure;
    protected double criticalTemperature;
    protected double criticalVolume;

    public Fluid(){

    }

    public double getCriticalPressure() {
        return criticalPressure;
    }
    public double getCriticalTemperature() {
        return criticalTemperature;
    }
    public double getCriticalVolume() {
        return criticalVolume;
    }
    public double getDensity() {
        return density;
    }
    public double getMolarMass() {
        return molarMass;
    }
    public double getMolarVolume() {
        return molarVolume;
    }
    public double getSpecificHeat() {
        return specificHeat;
    }
    public double getSpecificHeatCapacity() {
        return specificHeatCapacity;
    }
    public double getSpecificVolume() {
        return specificVolume;
    }
    public double getThermalConductivity() {
        return thermalConductivity;
    }
    public double getThermalExpansion() {
        return thermalExpansion;
    }
    public double getViscosity() {
        return viscosity;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCriticalPressure(double criticalPressure) {
        this.criticalPressure = criticalPressure;
    }
    public void setCriticalTemperature(double criticalTemperature) {
        this.criticalTemperature = criticalTemperature;
    }
    public void setCriticalVolume(double criticalVolume) {
        this.criticalVolume = criticalVolume;
    }
    public void setDensity(double density) {
        this.density = density;
    }
    public void setMolarMass(double molarMass) {
        this.molarMass = molarMass;
    }
    public void setMolarVolume(double molarVolume) {
        this.molarVolume = molarVolume;
    }
    public void setSpecificHeat(double specificHeat) {
        this.specificHeat = specificHeat;
    }
    public void setSpecificHeatCapacity(double specificHeatCapacity) {
        this.specificHeatCapacity = specificHeatCapacity;
    }
    public void setSpecificVolume(double specificVolume) {
        this.specificVolume = specificVolume;
    }
    public void setThermalConductivity(double thermalConductivity) {
        this.thermalConductivity = thermalConductivity;
    }
    public void setThermalExpansion(double thermalExpansion) {
        this.thermalExpansion = thermalExpansion;
    }
    public void setViscosity(double viscosity) {
        this.viscosity = viscosity;
    }
    public void setSpecificVolume(double molarVolume, double density){
        this.specificVolume = molarVolume/density;
    }
    public void setSpecificHeat(double molarMass, double density){
        this.specificHeat = molarMass/density;
    }
    public void setMolarVolume(double molarVolume, double density){
        this.molarVolume = molarVolume*density;
    }
    public void setMolarMass(double molarMass, double density){
        this.molarMass = molarMass*density;
    }
    public void setDensity(double molarVolume, double molarMass){
        this.density = molarVolume/molarMass;
    }
    public String toString(){
        return name;
    }
}
