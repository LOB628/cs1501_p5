package cs1501_p5;

public class SquaredEuclideanMetric implements DistanceMetric_Inter {

    @Override
    public double colorDistance(Pixel p1, Pixel p2) {
        int redDiff = p1.getRed() - p2.getRed();
        int greenDiff = p1.getGreen() - p2.getGreen();
        int blueDiff = p1.getBlue() - p2.getBlue();
        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }

}
