package cs1501_p5;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClusteringMapGenerator implements ColorMapGenerator_Inter {
    public static final int MAX_ITERATIONS = 1000;
    public boolean DEBUG = false;
    public boolean VERBOSE = true;
    DistanceMetric_Inter metric;

    public ClusteringMapGenerator(DistanceMetric_Inter metric) {
        this.metric = metric;
    }

    /**
     * Produces an initial palette.The initial palette will be the initial
     * centroids. When
     * needed, a distance metric should be specified when the color map
     * generator is constructed.
     *
     * @param pixelArray the 2D Pixel array that represents a bitmap image
     * @param numColors  the number of desired colors in the palette
     * @return a Pixel array containing numColors elements
     */
    @Override
    public Pixel[] generateColorPalette(Pixel[][] pixelArray, int numColors) {

        Pixel[] palette = new Pixel[numColors];
        HashSet<Pixel> paletteSet = new HashSet<Pixel>();
        palette[0] = pixelArray[0][0];
        paletteSet.add(palette[0]);

        HashSet<Pixel> colors = utils.uniqueColorsSet(pixelArray);
        if (colors.size() < numColors) {
            System.out.println(
                    "Warning: ClusteringMapGenerator.generateColorPalette() fewer unique colors than numColors");
        }
        for (int i = 1; i < numColors; i++) {
            Pixel centroid = null;
            double maxDistance = -1;
            for (Pixel p : colors) {
                if (paletteSet.contains(p)) {
                    continue;
                }
                double centroidMinDistance = Double.MAX_VALUE;
                Pixel minCentroid = null;
                for (Pixel q : paletteSet) {
                    double distance = metric.colorDistance(p, q);
                    if (distance < centroidMinDistance ||
                            (distance == centroidMinDistance &&
                                    utils.pixelToInt(q) > utils.pixelToInt(minCentroid))) {
                        centroidMinDistance = distance;
                        minCentroid = q;
                    }
                }
                if (centroidMinDistance > maxDistance
                        || (centroidMinDistance == maxDistance
                                && utils.pixelToInt(p) > utils.pixelToInt(centroid))) {
                    maxDistance = centroidMinDistance;
                    centroid = p;
                }
            }
            if (maxDistance != -1) {
                palette[i] = centroid;
                paletteSet.add(centroid);
            } else {
                if (VERBOSE) {
                    System.out.println(
                            "Warning: ClusteringMapGenerator.generateColorPalette() failed to find a centroid");
                }
            }
        }
        return palette;
    }

    @Override
    public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArray, Pixel[] initialColorPalette) {
        // Lloyd's algorithm
        Pixel[] colorPalette = initialColorPalette;
        HashSet<HashSet<Pixel>> priors = new HashSet<>();
        // Pixel[][] previousPalettes = new Pixel[2][colorPalette.length];// [1] should
        // not be checked outside of circular
        // // hue
        // // metric
        // if (metric.getClass() == CircularHueMetric.class) {
        // // priors.add(colorPalette);
        // previousPalettes[0] = initialColorPalette.clone();// shallow
        // }
        HashSet<Pixel> colorPaletteSet = new HashSet<Pixel>();
        for (Pixel p : colorPalette) {
            colorPaletteSet.add(p);
        }
        priors.add((HashSet<Pixel>) colorPaletteSet.clone());

        if (DEBUG || VERBOSE) {
            System.out.println("Metric is " + metric.getClass());
        }

        @SuppressWarnings("unchecked")
        ArrayList<Pixel>[] associatedPixels = new ArrayList[colorPalette.length];
        HashMap<Pixel, Integer> colorFreq = utils.colorsFreq(pixelArray);
        HashMap<Pixel, Integer> associatedCentroids = new HashMap<Pixel, Integer>();
        for (Pixel p : colorFreq.keySet()) {
            associatedCentroids.put(p, -1);
        }
        if (DEBUG) {
            System.out.print("initialColorPalette: ");
            for (int i = 0; i < colorPalette.length; i++) {
                System.out.print(colorPalette[i] + ",");
            }
            System.out.println();
        }

        if (colorPalette.length == 0) {
            System.out.println("Warning: ClusteringMapGenerator.generateColorMap() empty initialColorPalette");
            return new HashMap<Pixel, Pixel>();
        }
        int iterations = 0;
        while (iterations < MAX_ITERATIONS) {

            // assign clusters
            for (int i = 0; i < colorPalette.length; i++) {
                associatedPixels[i] = new ArrayList<Pixel>();
            }
            boolean changed = false;
            for (Pixel p : colorFreq.keySet()) {
                double minDistance = metric.colorDistance(p, colorPalette[0]);
                int minIndex = 0;
                for (int i = 1; i < colorPalette.length; i++) {
                    if (colorPalette[i] == null) {
                        if (VERBOSE) {
                            System.out
                                    .println(
                                            "Warning: ClusteringMapGenerator.generateColorMap() null colorPalette value");
                        }
                        break;// asume there are only null values after this point
                    }
                    double distance = metric.colorDistance(p, colorPalette[i]);
                    if (distance < minDistance
                            || (distance == minDistance
                                    && utils.pixelToInt(colorPalette[minIndex]) < utils.pixelToInt(colorPalette[i]))) {
                        minDistance = distance;
                        minIndex = i;
                    }
                }
                if (DEBUG) {
                    System.out.println("pixel: " + p + " minDistance: " + minDistance +

                            " colorPalette[" + minIndex + "]: " + colorPalette[minIndex]);
                }
                associatedPixels[minIndex].add(p);

                if (associatedCentroids.get(p) != minIndex) {
                    changed = true;
                    associatedCentroids.put(p, minIndex);
                }
            }
            if (!changed) {
                break;
            }
            // update centroids
            for (int i = 0; i < colorPalette.length; i++) {
                int total = 0;
                int redSum = 0;
                int greenSum = 0;
                int blueSum = 0;
                for (Pixel p : associatedPixels[i]) {
                    int weight = colorFreq.get(p);
                    redSum += p.getRed() * weight;
                    greenSum += p.getGreen() * weight;
                    blueSum += p.getBlue() * weight;
                    total += weight;
                }

                if (total == 0) {
                    if (DEBUG) {
                        System.out.println("No associated pixels for: " + colorPalette[i]);
                    }
                } else {
                    // No rounding or it fails tests
                    int redAvg = redSum / total;
                    int greenAvg = greenSum / total;
                    int blueAvg = blueSum / total;
                    colorPalette[i] = new Pixel(redAvg, greenAvg, blueAvg);

                }
            }
            colorPaletteSet = new HashSet<Pixel>();
            for (Pixel p : colorPalette) {
                colorPaletteSet.add(p);
            }

            if (priors.contains(colorPaletteSet)) {
                if (VERBOSE) {
                    System.out.println("Non-sequential palette convergence");
                }
                break;
            }
            priors.add((HashSet<Pixel>) colorPaletteSet.clone());
            // if (metric.getClass() == CircularHueMetric.class) {
            // boolean different = false;
            // for (int i = 0; i < colorPalette.length; i++) {
            // if (!colorPalette[i].equals(previousPalettes[0][i])) {
            // different = true;
            // break;
            // }
            // }
            // previousPalettes[0] = previousPalettes[1];
            // previousPalettes[1] = colorPalette.clone();// shallow

            // if (!different) {
            // if (DEBUG || VERBOSE) {
            // System.out.println("Circular Hue Metric: 2-Cycle Detected");
            // }
            // break;
            // }
            // // if (priors.contains(colorPalette)) {
            // // if (VERBOSE) {
            // // System.out.println("Palette Repeated !");
            // // }
            // // break;
            // // }
            // // priors.add(colorPalette);
            // }
            if (DEBUG) {
                System.out.print("colorPalette: ");
                for (int i = 0; i < colorPalette.length; i++) {
                    System.out.print(colorPalette[i] + ",");
                }
                System.out.println();
            }
            iterations++;
        }
        if (iterations == MAX_ITERATIONS) {
            System.out.println(
                    "Warning: ClusteringMapGenerator.generateColorMap() stopped at MAX_ITERATIONS:" + MAX_ITERATIONS);
        }
        if (DEBUG || VERBOSE) {
            System.out.println("iterations: " + iterations);
        }
        HashMap<Pixel, Pixel> colorMap = new HashMap<Pixel, Pixel>();
        for (int i = 0; i < colorPalette.length; i++) {
            for (Pixel p : associatedPixels[i]) {
                colorMap.put(p, colorPalette[i]);
            }
        }

        return colorMap;
    }

}
