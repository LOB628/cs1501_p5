/**
 * A driver for CS1501 Project 5
 * @author	Dr. Farnan
 */

package cs1501_p5;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.nio.file.Paths;
import java.nio.file.Files;

public class App {
    static Pixel[][] genStripedArr() {
        return new Pixel[][] {
                { new Pixel(5, 5, 5), new Pixel(5, 5, 5), new Pixel(5, 5, 5) },
                { new Pixel(50, 50, 50), new Pixel(50, 50, 50), new Pixel(50, 50, 50) },
                { new Pixel(100, 100, 100), new Pixel(100, 100, 100), new Pixel(100, 100, 100) },
                { new Pixel(150, 150, 150), new Pixel(150, 150, 150), new Pixel(150, 150, 150) },
                { new Pixel(200, 200, 200), new Pixel(200, 200, 200), new Pixel(200, 200, 200) },
                { new Pixel(250, 250, 250), new Pixel(250, 250, 250), new Pixel(250, 250, 250) }
        };
    }

    public static void main(String[] args) {

        // try {
        // // Load bitmap image
        // BufferedImage image = ImageIO.read(new File("image.bmp"));

        // // Create pixel matrix
        // Pixel[][] pixelMatrix = Util.convertBitmapToPixelMatrix(image);

        // // Save pixel matrix to file
        // Util.savePixelMatrixToBitmap("pixel_matrix.bmp", pixelMatrix);
        // } catch (Exception e) {
        // // This is very bad exception handling, but is only a proof of concept
        // e.printStackTrace();
        // }
        runOnDirectory(baseInputPath, 2, 4, 8, 16);

    }

    public static void clusterTest() {
        Pixel[][] pixelMatrix = genStripedArr();
        ColorMapGenerator_Inter gen = new ClusteringMapGenerator(new SquaredEuclideanMetric());
        Pixel[] palette = gen.generateColorPalette(pixelMatrix, 4);
        for (Pixel p : palette) {
            System.out.println(p);
        }
        ColorQuantizer quant = new ColorQuantizer(pixelMatrix, gen);
    }

    public static void bucketing1() {
        ColorMapGenerator_Inter gen = new BucketingMapGenerator();
        ColorQuantizer quant = new ColorQuantizer("image.bmp", gen);
        quant.quantizeToBMP("outputBucket.bmp", 8);
    }

    public static void cluster1() {
        ColorMapGenerator_Inter genEuclid = new ClusteringMapGenerator(new SquaredEuclideanMetric());
        ColorQuantizer quantEuclid = new ColorQuantizer("image.bmp", genEuclid);
        quantEuclid.quantizeToBMP("outputEuclid.bmp", 8);

        ColorMapGenerator_Inter genHue = new ClusteringMapGenerator(new CircularHueMetric());
        ColorQuantizer quantHue = new ColorQuantizer("image.bmp", genHue);
        quantHue.quantizeToBMP("outputHue.bmp", 8);

        // for (Pixel p : quant.DBG_initialPalette) {
        // System.out.println(p);
        // }
    }

    static final String id = "LOB41";
    static final String baseOutputPath = "outputs";
    static final String baseInputPath = "inputs";

    private static String getOutputPath(String name, String... fields) {
        String meta = "";
        String path = baseOutputPath + "/" + id + "/" + name;
        for (String field : fields) {
            meta += "_" + field;
            path += "/" + field;
        }
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Error creating directory: " + path);
            e.printStackTrace();
            System.exit(1);
        }

        return path + "/" + id + "_" + name + "_" + meta + ".bmp";
    }

    public static void runOnImage(String filename, int numColors) {
        String filename_noextension = filename.substring(0, filename.length() - 4);// .bmp only
        String filePath = baseInputPath + "/" + filename;
        ColorMapGenerator_Inter genEuclid = new ClusteringMapGenerator(new SquaredEuclideanMetric());
        ColorQuantizer quantEuclid = new ColorQuantizer(filePath, genEuclid);
        quantEuclid.quantizeToBMP(getOutputPath(filename_noextension, "euclid", "n" + numColors), numColors);
        ColorMapGenerator_Inter genHue = new ClusteringMapGenerator(new CircularHueMetric());
        ColorQuantizer quantHue = new ColorQuantizer(filePath, genHue);
        quantHue.quantizeToBMP(getOutputPath(filename_noextension, "hue", "n" + numColors), numColors);
        ColorMapGenerator_Inter genBucket = new BucketingMapGenerator();
        ColorQuantizer quantBucket = new ColorQuantizer(filePath, genBucket);
        quantBucket.quantizeToBMP(getOutputPath(filename_noextension, "bucket", "n" + numColors), numColors);
    }

    public static void runOnDirectory(String dir, int... numColors) {
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            String filename = file.getName();
            if (filename.endsWith(".bmp")) {
                for (int n : numColors) {
                    System.out.println("Running on " + filename + " with " + n + " colors");
                    runOnImage(filename, n);
                }
            }

        }
    }

}
