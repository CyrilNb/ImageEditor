package projet_techno_l3.imageeditor.ImageModifications;

public class Histogram {

    private int[] values;
    private int minIntensity;
    private int maxIntensity;

    public Histogram(int[] values, int minIntensity, int maxIntensity) {
        this.values = values;
        this.minIntensity = minIntensity;
        this.maxIntensity = maxIntensity;
    }
}
