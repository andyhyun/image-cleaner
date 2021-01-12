import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.io.*;

public class ImageCleaner {

    public static void main(String[] args) throws IOException {
        File picsFolder = new File(System.getProperty("user.dir") + "/pics");
        File[] fileList = picsFolder.listFiles();
        if (fileList == null) {
            System.out.println("There must be image files in a sibling folder to ImageCleaner.java, named 'pics'.");
            System.exit(0);
        }
        int numPics = fileList.length;
        BufferedImage[] picList = new BufferedImage[numPics];
        for (int picIndex = 0; picIndex < numPics; picIndex++) {
            BufferedImage temp_image = ImageIO.read(fileList[picIndex]);
            picList[picIndex] = temp_image;
        }
        int height = picList[0].getHeight();
        int width = picList[0].getWidth();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int tolerance = 8;

        Scanner sc = new Scanner(System.in);
        System.out.println("Input just the number next to the algorithm that you want to use:");
        System.out.println("\t1) Median\n\t2) Modal Class");
        int mode = sc.nextInt();

        if (mode == 2) {
            System.out.println("Enter the class interval, must be one of: 1, 2, 4, 8, 16 (recommended), 32, 64, 128");
            tolerance = sc.nextInt();
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (mode) {
                    case 1 -> output.setRGB(x, y, medianARGB(x, y, picList));
                    case 2 -> output.setRGB(x, y, groupModeARGB(x, y, picList, tolerance));
                }
            }
        }
        File outputFile = new File("output.png");
        ImageIO.write(output, "png", outputFile);
        System.out.println("The file 'output.png' has been created in the same directory as 'ImageCleaner.java'.");
    }

    private static int medianARGB(int x, int y, BufferedImage[] picList) {
        int numPics = picList.length;
        int[] redVals = new int[numPics];
        int[] greenVals = new int[numPics];
        int[] blueVals = new int[numPics];
        for (int picIndex = 0; picIndex < numPics; picIndex++) {
            int clr = picList[picIndex].getRGB(x, y);
            int red = ((clr & 0x00ff0000) >> 16);
            int green = ((clr & 0x0000ff00) >> 8);
            int blue = (clr & 0x000000ff);
            redVals[picIndex] = red;
            greenVals[picIndex] = green;
            blueVals[picIndex] = blue;
        }
        Arrays.sort(redVals);
        Arrays.sort(greenVals);
        Arrays.sort(blueVals);
        if (numPics % 2 == 0) {
            int leftMidPos = numPics / 2 - 1;
            int rightMidPos = numPics / 2;
            int redMedian = (redVals[leftMidPos] + redVals[rightMidPos]) / 2;
            int greenMedian = (greenVals[leftMidPos] + greenVals[rightMidPos]) / 2;
            int blueMedian = (blueVals[leftMidPos] + blueVals[rightMidPos]) / 2;
            return colorToARGB(255, redMedian, greenMedian, blueMedian);
        } else {
            int midPos = numPics / 2;
            int redMedian = redVals[midPos];
            int greenMedian = greenVals[midPos];
            int blueMedian = blueVals[midPos];
            return colorToARGB(255, redMedian, greenMedian, blueMedian);
        }
    }

    private static int groupModeARGB(int x, int y, BufferedImage[] picList, int tolerance) {
        int[] redValCount = new int[256 / tolerance];
        int[] greenValCount = new int[256 / tolerance];
        int[] blueValCount = new int[256 / tolerance];
        for (BufferedImage bufferedImage : picList) {
            int clr = bufferedImage.getRGB(x, y);
            int red = ((clr & 0x00ff0000) >> 16);
            int green = ((clr & 0x0000ff00) >> 8);
            int blue = (clr & 0x000000ff);
            redValCount[red / tolerance]++;
            greenValCount[green / tolerance]++;
            blueValCount[blue / tolerance]++;
        }
        int redGroupMode;
        int greenGroupMode;
        int blueGroupMode;
        if (tolerance == 1) {
            redGroupMode = intArrayMax(redValCount);
            greenGroupMode = intArrayMax(greenValCount);
            blueGroupMode = intArrayMax(blueValCount);
        } else {
            redGroupMode = (tolerance / 2 - 1) + tolerance * intArrayMax(redValCount);
            greenGroupMode = (tolerance / 2 - 1) + tolerance * intArrayMax(greenValCount);
            blueGroupMode = (tolerance / 2 - 1) + tolerance * intArrayMax(blueValCount);
        }
        return colorToARGB(255, redGroupMode, greenGroupMode, blueGroupMode);
    }

    private static int colorToARGB(int alpha, int red, int green, int blue) {
        int argb = 0;
        argb += alpha;
        argb = argb << 8;
        argb += red;
        argb = argb << 8;
        argb += green;
        argb = argb << 8;
        argb += blue;
        return argb;
    }

    private static int intArrayMax(int[] arr) {
        int max = Integer.MIN_VALUE;
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                index = i;
            }
        }
        return index;
    }
}
