package cs1501_p5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import javax.imageio.ImageIO;

public class ColorQuantizer implements ColorQuantizer_Inter {
    /**
     * Performs color quantization using the color map generator specified when
     * this quantizer was constructed.
     *
     * @param numColors number of colors to use for color quantization
     * @return A two dimensional array where each index represents the pixel
     *         from the original bitmap image and contains a Pixel representing its
     *         color after quantization
     */
    Pixel[][] pixelArray;
    ColorMapGenerator_Inter gen;
    public Pixel[] DBG_initialPalette;
    public Map<Pixel, Pixel> DBG_colorMap;

    public ColorQuantizer(Pixel[][] pixelArray, ColorMapGenerator_Inter gen) {
        this.pixelArray = pixelArray;
        this.gen = gen;
    }

    public ColorQuantizer(String bmpFilename, ColorMapGenerator_Inter gen) {
        try {
            this.pixelArray = Util.convertBitmapToPixelMatrix(ImageIO.read(new File(bmpFilename)));
            this.gen = gen;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pixel[][] quantizeTo2DArray(int numColors) {
        Pixel[] palette = gen.generateColorPalette(pixelArray, numColors);
        DBG_initialPalette = palette;
        Map<Pixel, Pixel> map = gen.generateColorMap(pixelArray, palette);
        DBG_colorMap = map;
        Pixel[][] quantizedPixels = new Pixel[pixelArray.length][pixelArray[0].length];
        for (int i = 0; i < pixelArray.length; i++) {
            for (int j = 0; j < pixelArray[0].length; j++) {
                quantizedPixels[i][j] = map.get(pixelArray[i][j]);
            }
        }
        return quantizedPixels;
    }

    /**
     * Performs color quantization using the color map generator specified when
     * this quantizer was constructed. Rather than returning the pixel array,
     * this method writes the resulting image in bmp format to the specified
     * file.
     *
     * @param numColors number of colors to use for color quantization
     * @param fileName  File to write resulting image to
     */
    @Override
    public void quantizeToBMP(String fileName, int numColors) {
        Util.savePixelMatrixToBitmap(fileName, quantizeTo2DArray(numColors));
    }

    public void getColorPalette(String fileName, int numColors) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            Pixel[] colorPalette = gen.generateColorPalette(pixelArray, numColors);
            for (Pixel p : colorPalette) {
                String color = p == null ? "null" : p.toString();
                writer.write(color);
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
