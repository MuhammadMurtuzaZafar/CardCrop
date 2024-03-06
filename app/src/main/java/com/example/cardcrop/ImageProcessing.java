package com.example.cardcrop;

import static com.example.cardcrop.MainActivity.imageView;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageProcessing {
    public  Bitmap drawAndCropLargestContour(Bitmap inputBitmap) {
        // Convert Bitmap to Mat
        Mat imageMat = new Mat();
        Utils.bitmapToMat(inputBitmap, imageMat);

        // Convert to grayscale
        Mat grayMat = new Mat();
        Imgproc.cvtColor(imageMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        // Detect edges using Canny
        Mat edges = new Mat();
        Imgproc.Canny(grayMat, edges, 50, 150);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Draw contours on the image
        Mat imageWithContours = new Mat();
        imageMat.copyTo(imageWithContours);

        // Draw all contours in green color
        // Imgproc.drawContours(imageWithContours, contours, -1, new Scalar(0, 255, 0), 2);

        // Draw the largest contour in blue color
        List<MatOfPoint> largestContourList = new ArrayList<>();
        // Find largest contour
        MatOfPoint largestContour = getMaxContour(contours);
        largestContourList.add(largestContour);
        Imgproc.drawContours(imageWithContours, largestContourList, -1, new Scalar(0, 255, 0), 12);

        // Crop image based on largest contour
        Rect boundingRect = Imgproc.boundingRect(largestContour);
        Mat croppedImage = new Mat(imageMat, boundingRect);

        // imageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        imageView.setMaxCropResultSize(boundingRect.width,boundingRect.height);
        // Set crop rectangle (left, top, width, height)
//        imageView.setCropRect(convertRect(boundingRect));
        //  imageView.
        //       imageView.setImageBitmap(inputBitmap);
        //imageView.setAutoZoomEnabled(false);
        // Convert cropped Mat back to Bitmap
        Bitmap croppedBitmap = Bitmap.createBitmap(imageWithContours.cols(), imageWithContours.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageWithContours, croppedBitmap);

        return croppedBitmap;
    }
    public static android.graphics.Rect convertRect(Rect rect) {
        // Convert OpenCV's Rect to Android's Rect
        return new android.graphics.Rect(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);
    }
    public  MatOfPoint getMaxContour(List<MatOfPoint> contours) {

        if (contours.isEmpty()) {
            return null;
        }
        MatOfPoint maxContour = contours.get(0);
        double maxArea = Double.MIN_VALUE;

        for (MatOfPoint contour : contours) {
            // Convert MatOfPoint to MatOfPoint2f for approximation
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double epsilon = 0.01 * Imgproc.arcLength(contour2f, true);

            // Approximate contour to a polygon
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);

            // Convert MatOfPoint2f back to MatOfPoint for area calculation
            MatOfPoint approxContour = new MatOfPoint(approxCurve.toArray());

            // Calculate the area of the approximated contour
            double area = Imgproc.contourArea(approxContour);

            // Update the maximum contour if a larger area is found
            if (area > maxArea && approxContour.rows()>2) {
                maxArea = area;
                maxContour = contour;
            }
        }


        return maxContour;
    }

}
