package Utilites;

public class UnitConverter {

    // Temperature Conversions
    public static double celsiusToKelvin(double celsius) {
        return celsius + 273.15;
    }

    public static double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    public static double celsiusToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }

    public static double fahrenheitToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    public static double kelvinToFahrenheit(double kelvin) {
        return celsiusToFahrenheit(kelvinToCelsius(kelvin));
    }

    public static double fahrenheitToKelvin(double fahrenheit) {
        return celsiusToKelvin(fahrenheitToCelsius(fahrenheit));
    }

    // Pressure Conversions
    public static double pascalToBar(double pascal) {
        return pascal / 100000.0;
    }

    public static double barToPascal(double bar) {
        return bar * 100000.0;
    }

    public static double pascalToMPa(double pascal) {
        return pascal / 1_000_000.0;
    }

    public static double MPaToPascal(double MPa) {
        return MPa * 1_000_000.0;
    }

    public static double barToMPa(double bar) {
        return bar / 10.0;
    }

    public static double MPaToBar(double MPa) {
        return MPa * 10.0;
    }

    public static double atmToPascal(double atm) {
        return atm * 101325.0;
    }

    public static double pascalToAtm(double pascal) {
        return pascal / 101325.0;
    }

    // Energy Conversions
    public static double jouleToKiloJoule(double joule) {
        return joule / 1000.0;
    }

    public static double kiloJouleToJoule(double kiloJoule) {
        return kiloJoule * 1000.0;
    }

    public static double jouleToCalorie(double joule) {
        return joule / 4.184;
    }

    public static double calorieToJoule(double calorie) {
        return calorie * 4.184;
    }

    public static double jouleToBTU(double joule) {
        return joule / 1055.06;
    }

    public static double BTUToJoule(double BTU) {
        return BTU * 1055.06;
    }

    public static double kiloJouleToBTU(double kiloJoule) {
        return jouleToBTU(kiloJouleToJoule(kiloJoule));
    }

    public static double BTUToKiloJoule(double BTU) {
        return jouleToKiloJoule(BTUToJoule(BTU));
    }

    // Length Conversions
    public static double meterToMillimeter(double meter) {
        return meter * 1000.0;
    }

    public static double millimeterToMeter(double millimeter) {
        return millimeter / 1000.0;
    }

    public static double meterToCentimeter(double meter) {
        return meter * 100.0;
    }

    public static double centimeterToMeter(double centimeter) {
        return centimeter / 100.0;
    }

    public static double meterToKilometer(double meter) {
        return meter / 1000.0;
    }

    public static double kilometerToMeter(double kilometer) {
        return kilometer * 1000.0;
    }

    public static double meterToInch(double meter) {
        return meter * 39.3701;
    }

    public static double inchToMeter(double inch) {
        return inch / 39.3701;
    }

    public static double meterToFoot(double meter) {
        return meter * 3.28084;
    }

    public static double footToMeter(double foot) {
        return foot / 3.28084;
    }

    // Volume Conversions
    public static double literToMilliliter(double liter) {
        return liter * 1000.0;
    }

    public static double milliliterToLiter(double milliliter) {
        return milliliter / 1000.0;
    }

    public static double literToCubicMeter(double liter) {
        return liter / 1000.0;
    }

    public static double cubicMeterToLiter(double cubicMeter) {
        return cubicMeter * 1000.0;
    }

    // Mass Conversions
    public static double kilogramToGram(double kilogram) {
        return kilogram * 1000.0;
    }

    public static double gramToKilogram(double gram) {
        return gram / 1000.0;
    }

    public static double kilogramToTon(double kilogram) {
        return kilogram / 1000.0;
    }

    public static double tonToKilogram(double ton) {
        return ton * 1000.0;
    }

    public static double poundToKilogram(double pound) {
        return pound / 2.20462;
    }

    public static double kilogramToPound(double kilogram) {
        return kilogram * 2.20462;
    }

    public static double roundToDecimals(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return Math.round(value * scale) / scale;
    }
}

