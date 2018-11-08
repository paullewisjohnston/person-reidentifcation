package DataAugmentation;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import static org.opencv.highgui.Highgui.imwrite;

// ==============================================================================
// Author:          PaulLewisJohnston
// University:      Queen's University Belfast
// Description:     This PerformDataAugmentation Class reads in the Amazon
//                  person dataset, performs the various augmentations
//                  and outputs the finalised augmented dataset in local
//                  directory /datasetAugmented/Augmented
// ==============================================================================

public class PerformDataAugmentation {

    public static void Run() {
        //final DecimalFormat decimalFormat = new DecimalFormat("0000");
        int index;
        int personIndexStart = 633;
        int j,k;
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            //Read in person images downloaded via webscraper
            FileReader fileReaderPersonA = new FileReader("dataset/person/personA");
            List<Mat> imgListPersonA = fileReaderPersonA.imgList();

            FileReader fileReaderPersonB = new FileReader("dataset/person/personB");
            List<Mat> imgListPersonB = fileReaderPersonB.imgList();

            //Read in background images
            FileReader fileReaderBg = new FileReader("dataset/background");
            List<Mat> imgListBg = fileReaderBg.imgList();

            //Remove Background
            List<Mat> personTransparentA1 = DataAugmentation.removeBackground(imgListPersonA);
            List<Mat> personTransparentB1 = DataAugmentation.removeBackground(imgListPersonB);
            System.out.println("Amazon Background Removed");

            //Perform combination of random rotation and random cropping
            List<Mat> personTransparentA2 = DataAugmentation.augmentRandomTransform(personTransparentA1);
            List<Mat> personTransparentB2 = DataAugmentation.augmentRandomTransform(personTransparentB1);
            System.out.println("Person Augmented: Random Crop & Rotate");

            //Perform mirroring
            List<Mat> personTransparentA3 = DataAugmentation.augmentMirror(personTransparentA1);
            List<Mat> personTransparentB3 = DataAugmentation.augmentMirror(personTransparentB1);
            System.out.println("Person Augmented: Mirrored");

            //
            List<Mat> resizedA1 = DataAugmentation.resizeCropImage(personTransparentA1);
            List<Mat> resizedB1 = DataAugmentation.resizeCropImage(personTransparentB1);
            List<Mat> resizedA2 = DataAugmentation.resizeCropImage(personTransparentA2);
            List<Mat> resizedB2 = DataAugmentation.resizeCropImage(personTransparentB2);
            List<Mat> resizedA3 = DataAugmentation.resizeCropImage(personTransparentA3);
            List<Mat> resizedB3 = DataAugmentation.resizeCropImage(personTransparentB3);
            System.out.println("Image Resized and Cropped");

            List<Mat> augmentedImageA1 = DataAugmentation.addRandomBackground(imgListBg, resizedA1);
            List<Mat> augmentedImageB1 = DataAugmentation.addRandomBackground(imgListBg, resizedB1);
            List<Mat> augmentedImageA2 = DataAugmentation.addRandomBackground(imgListBg, resizedA2);
            List<Mat> augmentedImageB2 = DataAugmentation.addRandomBackground(imgListBg, resizedB2);
            List<Mat> augmentedImageA3 = DataAugmentation.addRandomBackground(imgListBg, resizedA3);
            List<Mat> augmentedImageB3 = DataAugmentation.addRandomBackground(imgListBg, resizedB3);
            System.out.println("Artificial Backgrounds Added");


            for(int i = 0; i<imgListPersonA.size();i++)
            {
                index = i + personIndexStart;
                if (i>=0 && i<20)
                {
                    imwrite("datasetAugmented/transparent/" + index + "_1.png", personTransparentA1.get(i));
                    imwrite("datasetAugmented/transparent/" + index + "_2.png", personTransparentB1.get(i));
                    imwrite("datasetAugmented/transparent/" + index + "_3.png", personTransparentA2.get(i));
                    imwrite("datasetAugmented/transparent/" + index + "_4.png", personTransparentB2.get(i));
                    imwrite("datasetAugmented/transparent/" + index + "_5.png", personTransparentA3.get(i));
                    imwrite("datasetAugmented/transparent/" + index + "_6.png", personTransparentB3.get(i));

                    imwrite("datasetAugmented/resized/" + index + "_1.png", resizedA1.get(i));
                    imwrite("datasetAugmented/resized/" + index + "_2.png", resizedB1.get(i));
                    imwrite("datasetAugmented/resized/" + index + "_3.png", resizedA2.get(i));
                    imwrite("datasetAugmented/resized/" + index + "_4.png", resizedB2.get(i));
                    imwrite("datasetAugmented/resized/" + index + "_5.png", resizedA3.get(i));
                    imwrite("datasetAugmented/resized/" + index + "_6.png", resizedB3.get(i));

                    imwrite("datasetAugmented/augmented/" + index + "_1.png", augmentedImageA1.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_2.png", augmentedImageB1.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_3.png", augmentedImageA2.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_4.png", augmentedImageB2.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_5.png", augmentedImageA3.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_6.png", augmentedImageB3.get(i));
                }
                else{
                    imwrite("datasetAugmented/augmented/" + index + "_1.png", augmentedImageA1.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_2.png", augmentedImageB1.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_3.png", augmentedImageA2.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_4.png", augmentedImageB2.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_5.png", augmentedImageA3.get(i));
                    imwrite("datasetAugmented/augmented/" + index + "_6.png", augmentedImageB3.get(i));
                }
            }

            System.out.println("Data Augmentation Complete");
        } catch (Exception e) {
            System.out.println("error main:" + e.getMessage());
        }
    }
}
