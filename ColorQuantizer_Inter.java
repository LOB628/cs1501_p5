/**
 * Color Quantizer interface for CS1501 Project 5
 *
 * @author Dr. Farnan
 * @author Brian T. Nixon
 * @author Marcelo d'Almeida
 */
package cs1501_p5;

interface ColorQuantizer_Inter {
    /**
     * Performs color quantization using the color map generator specified when
     * this quantizer was constructed.
     *
     * @param numColors number of colors to use for color quantization
     * @return A two dimensional array where each index represents the pixel
     *         from the original bitmap image and contains a Pixel representing its
     *         color after quantization
     */
    public Pixel[][] quantizeTo2DArray(int numColors);

    public void quantizeToBMP(String fileName, int numColors);
}
