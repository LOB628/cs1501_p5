package cs1501_p5;

public class CircularHueMetric implements DistanceMetric_Inter {
    @Override
    public double colorDistance(Pixel p1, Pixel p2) {
        double hue1 = p1.getHue();
        double hue2 = p2.getHue();
        double diff = Math.abs(hue1 - hue2);
        if (diff > 180) {
            diff = 360 - diff;
        }
        return diff;
    }

}
