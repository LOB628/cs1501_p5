package cs1501_p5;

import java.util.HashMap;
import java.util.HashSet;

public class utils {
    // public static int pixelToInt(Pixel p) {
    // return (p.getRed() << 16) + (p.getGreen() << 8) + p.getBlue();
    // }
    public static int pixelToInt(Pixel pix) {
        return ((pix.getRed() << 16) & 0xff0000) | ((pix.getGreen() << 8) & 0xff00) | ((pix.getBlue() & 0xff));
    }

    public static Pixel intToPixel(int color) {
        int[] rgb = new int[3];
        for (int j = 0; j < 3; j++) {
            rgb[j] = (color >> (16 - 8 * j)) & 0x0000FF;
        }
        return new Pixel(rgb[0], rgb[1], rgb[2]);
    }

    public static HashMap<Pixel, Integer> colorsFreq(Pixel[][] pixelArray) {
        HashMap<Pixel, Integer> colorMap = new HashMap<Pixel, Integer>();
        for (int i = 0; i < pixelArray.length; i++) {
            for (int j = 0; j < pixelArray[0].length; j++) {
                Pixel p = pixelArray[i][j];
                if (colorMap.containsKey(p)) {
                    colorMap.put(p, colorMap.get(p) + 1);
                } else {
                    colorMap.put(p, 1);
                }
            }
        }
        return colorMap;
    }

    public static HashSet<Pixel> uniqueColorsSet(Pixel[][] pixelArray) {
        HashSet<Pixel> colorSet = new HashSet<Pixel>();
        for (int i = 0; i < pixelArray.length; i++) {
            for (int j = 0; j < pixelArray[0].length; j++) {
                colorSet.add(pixelArray[i][j]);
            }
        }
        return colorSet;
    }

}
