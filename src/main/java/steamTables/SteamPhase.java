package steamTables;

public enum SteamPhase {
    SaturatedLiquid("Saturated Liquid",0),
    SaturatedVapour("Saturated Vapor", 1),
    SaturatedMixture("Saturated Mixture", 1.5),
    CompressedLiquid("Compressed Liquid", 0),
    SuperHeatedWater("Superheated Water", 1),
    Saturated("Saturated",0.5);

    private final String phaseName;

    SteamPhase(String phaseName, double i) {
        this.phaseName = phaseName;
    }

    @Override
    public String toString() {
        return this.phaseName;
    }

    public double getX() {
        return this.ordinal();
    }

    public static SteamPhase getPhase(String value) {
        if (value.equals("Saturated Liquid")) {
            return SteamPhase.SaturatedLiquid;
        }
        if (value.equals("Saturated Vapour")) {
            return SteamPhase.SaturatedVapour;
        }
        if (value.equals("Saturated Mixture")) {
            return SteamPhase.SaturatedMixture;
        }
        if (value.equals("Superheated Water")) {
            return SteamPhase.SuperHeatedWater;
        }
        if (value.equals("Saturated")) {
            return SteamPhase.Saturated;
        }

        return SteamPhase.CompressedLiquid;
    }
}
