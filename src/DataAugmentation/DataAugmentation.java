package DataAugmentation;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.opencv.core.Core.flip;
import static org.opencv.imgproc.Imgproc.*;

// ==============================================================================
// Author:          PaulLewisJohnston
// University:      Queen's University Belfast
// Email:           pjohnston36@qub.ac.uk
// Description:     This DataAugmentation Class contains the set of
//                  augmentation functions using the OpenCv libraries
// ==============================================================================

public class DataAugmentation{

    static List<Mat> removeBackground(List<Mat> personImg) {

        List<Mat> resultList = new ArrayList<Mat>();
        for (Mat img : personImg) {
            Mat result = new Mat(img.rows(), img.cols(), img.type());
            Mat grey = new Mat(img.rows(), img.cols(), img.type());
            Mat alpha = new Mat(img.rows(), img.cols(), img.type());

            Imgproc.cvtColor(img, grey, COLOR_BGR2GRAY);
            Imgproc.threshold(grey, alpha, 250, 255, THRESH_BINARY_INV);

            //Blur to blend edges when background is substituted
            Imgproc.blur(alpha, alpha, new Size(2, 2));

            // dilate to fill gaps, erode to smooth edges
            Imgproc.dilate(alpha, alpha, new Mat(), new Point(-1, -1), 3);
            Imgproc.erode(alpha, alpha, new Mat(), new Point(-1, -1), 5);


            List<Mat> rgb = new ArrayList<Mat>(3);
            Core.split(img, rgb);

            //Merge mask as alpha channel to make background transparent
            List<Mat> rgba = new ArrayList<Mat>(4);
            rgba.add(rgb.get(0));
            rgba.add(rgb.get(1));
            rgba.add(rgb.get(2));
            rgba.add(alpha);
            Core.merge(rgba, result);

            resultList.add(result);
        }
        return resultList;
    }

    static List<Mat> addRandomBackground(List<Mat> backgroundList, List<Mat> foregroundList){

        List<Mat> result = new ArrayList<Mat>();
        Random rn = new Random();
        int width = 48;
        int height = 128;

        for(Mat img : foregroundList) {
            //select random background from list
            int index = rn.nextInt(backgroundList.size());
            Mat bg = backgroundList.get(index);
            int bgWidth = bg.width();
            int bgHeight = bg.height();

            //Perform random cropping of background to be substituted
            int max_start_x = bgWidth-width;
            int max_start_y = bgHeight-height;
            int bgstart_x = rn.nextInt(max_start_x+1);
            int bgstart_y = rn.nextInt(max_start_y+1);
            Mat cropped_bg = performCrop(bg, bgstart_x,bgstart_y,width,height);

            Mat merge = overlayImage(cropped_bg, img);
            result.add(merge);
        }
        return result;
    }

    static List<Mat> resizeCropImage(List<Mat> imgList){

        List<Mat> result = new ArrayList<Mat>();
        int width = 48;
        int height = 128;

        for(Mat img : imgList) {

            //Resize image keep same ratio
            int originalWidth = img.width();
            int originalHeight = img.height();
            int newHeight = height;
            int newWidth = (newHeight * originalWidth) / originalHeight;
            Mat resizedImage = resizeImage(img, newWidth,newHeight);

            int imgstart_x = (resizedImage.width()/2) - (width/2);
            int imgstart_y = (resizedImage.height()/2) - (height/2);

            //Crop image
            Mat cropped_img = performCrop(resizedImage, imgstart_x, imgstart_y, width, height);
            result.add(cropped_img);
        }
        return result;
    }

    static List<Mat> augmentRandomTransform(List<Mat> imgList){

        //Performs combination of random rotation and transformation
        List<Mat> randomRotate = DataAugmentation.augmentRandomRotate(imgList);
        List<Mat> randomTransform = DataAugmentation.augmentRandomCrop(randomRotate);
        return randomTransform;
    }

    static List<Mat> augmentRandomCrop(List<Mat> imgList){

        List<Mat> result = new ArrayList<Mat>();
        Random rn = new Random();
        int imgstart_x;
        int imgstart_y;
        int border = 20;

        for(Mat img : imgList) {
            Mat dst = new Mat(img.rows(), img.cols(), img.type());
            //Add border to image to allow crop position to extend past original height/width
            copyMakeBorder(img, dst, border, border, border, border, BORDER_CONSTANT);
            //Crop image at random (position +/- up to 20 pixels)
            imgstart_x = rn.nextInt((border*2)+1);
            imgstart_y = rn.nextInt((border*2)+1);
            Mat cropped_img = performCrop(dst, imgstart_x, imgstart_y, img.width(), img.height());
            result.add(cropped_img);
        }
        return result;
    }

    static List<Mat> augmentRandomRotate(List<Mat> imgList){

        List<Mat> result = new ArrayList<Mat>();
        Random rn = new Random();
        double angle;

        for(Mat img : imgList) {
            //Rotate images at random degree between -20 and 20
            Mat rotated = new Mat(img.rows(), img.cols(), img.type());
            Point center = new Point(img.cols()/2, img.rows()/2);
            angle = -20.0 + rn.nextInt(41);
            Mat rot_mat = Imgproc.getRotationMatrix2D(center, angle, 1.0);
            Imgproc.warpAffine(img, rotated, rot_mat, img.size());
            result.add(rotated);
        }
        return result;
    }

    static List<Mat> augmentMirror(List<Mat> imgList){

        List<Mat> result = new ArrayList<Mat>();
        for(Mat img : imgList) {
            //Produce mirror image by flipping rows
            Mat mirror = new Mat(img.rows(), img.cols(), img.type());
            flip(img, mirror, 1);
            result.add(mirror);
        }
        return result;
    }

    static Mat performCrop(Mat img, int start_x, int start_y, int width, int height){
        Rect rectCrop = new Rect(start_x, start_y, width, height);
        Mat croppedImg = new Mat(img, rectCrop);
        return croppedImg;
    }

    static Mat resizeImage(Mat img, int width, int height){
        Mat resizedImage = new Mat();
        Size sz = new Size(width,height);
        Imgproc.resize(img, resizedImage, sz);
        return resizedImage;
    }


    private static Mat overlayImage( Mat background, Mat foreground ) {
        Mat destination = new Mat( background.size(), background.type() );

        for ( int y = 0; y < ( int )( background.rows() ); ++y ) {
            for ( int x = 0; x < ( int )( background.cols() ); ++x ) {
                double b[] = background.get( y, x );
                double f[] = foreground.get( y, x );

                double alpha = f[3] / 255.0;

                double d[] = new double[3];
                for ( int k = 0; k < 3; ++k ) {
                    d[k] = f[k] * alpha + b[k] * ( 1.0 - alpha );
                }
                destination.put( y, x, d );
            }
        }
        return destination;
    }
}