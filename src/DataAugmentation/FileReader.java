package DataAugmentation;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

// ==============================================================================
// Author:          PaulLewisJohnston
// University:      Queen's University Belfast
// Email:           pjohnston36@qub.ac.uk
// Description:     This FileReader Class reads in the images at the specified
//                  filepath and provides the function to return them as a
//                  list of matrices.
// ==============================================================================

public class FileReader {

    public FileReader(String path) {
        // Directory path
        dir = new File(path);
    }
        // Directory path
        static File dir;

        // supported image extensions
        static final String[] EXTENSIONS = new String[]{
                "jpg", "png"
        };

        // filter to identify images based on their extensions
        static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                for (final String ext : EXTENSIONS) {
                    if (name.endsWith("." + ext)) {
                        return (true);
                    }
                }
                return (false);
            }
        };

    //Return list of type Mat for all images in folder
    public static List<Mat> imgList(){
        List<Mat> imgList = new ArrayList<>();
        if (dir.isDirectory()) { // make sure it's a directory
            int i = 0;
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
                Mat img = null;
                try {
                    img= Highgui.imread(dir+"/"+f.getName(), Highgui.CV_LOAD_IMAGE_COLOR);
                    imgList.add(img);
                    i++;
                }
                catch (Exception e) {System.out.println("error reading file: " + e.getMessage());}
            }
            return imgList;
        }
        System.out.println("error directory path invalid ");
        return null;
    }
}
