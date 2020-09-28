package noah.raytracer;

import javafx.application.Application;
// import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws FileNotFoundException {

        // Define the screen dimensions
        int width = 1500;
        int height = 900;

        Vector ambientReflectionInt = new Vector(1,1,1);
        Vector reflectionCoef = new Vector(0.1F,0.1F,0.1F);

        // Define the objects in the screen
        RenderObject[] objects = new RenderObject[] {
                /* radius, center, ambientCo, diffuseCo, specularCo, reflectCo */
                new Sphere(100, new Vector(0,0,-300), new Vector(0.96F,0.76F, 0.76F), new Vector(1F,1F, 1F),
                        new Vector(0.00008F,0.00008F,0.00008F), reflectionCoef),

                new Sphere(40, new Vector(-150,100,-450), new Vector(0.52F,0.80F, 0.79F), new Vector(0.5F,0.5F, 0.5F),
                        new Vector(0.00005F,0.00005F,0.00005F), reflectionCoef),

                new Sphere(40, new Vector(150,-100,-450), new Vector(0.25F,0.70F, 0.63F), new Vector(0.5F,0.5F, 0.5F),
                        new Vector(0.00005F,0.00005F,0.00005F), reflectionCoef),

                new Sphere(40, new Vector(-150,-100,-150), new Vector(0.76F,0.55F, 0.61F), new Vector(0.5F,0.5F, 0.5F),
                        new Vector(0.00005F,0.00005F,0.00005F), reflectionCoef),

                new Sphere(40, new Vector(150,100,-150), new Vector(0.90F,0.65F, 0.48F), new Vector(0.5F,0.5F, 0.5F),
                        new Vector(0.00005F,0.00005F,0.00005F), reflectionCoef),

                new PointLight(20, new Vector(300,290,-140), new Vector(0.49F, 0.77F, 0.72F),
                        new Vector(0,0,0), new Vector(0.1F,0.1F,1F), new Vector(0,0,0),
                        new Vector(0,0,0))
        };

        // Render the image
        ImageView view = new ImageView(render(width, height, objects, ambientReflectionInt, 10F, 0.04F));

        // Output the image
        StackPane root = new StackPane(view);
        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.show();

        System.out.println("Done!");

    }

    private Image render(int width, int height, RenderObject[] objects, Vector ambientReflection,
                         float specularInt, float devScale) {

        // 2D array of pixels
        Vector[][] screen = new Vector[width][height];

        // Find the center point in the screen
        int midPointX = width/2;
        int midPointY = height/2;

        // For each pixel, send a ray
        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                // Deviate the x and y from the midpoint
                // Starting from the left of the midpoint to the right
                float deviationX = (midPointX - x) * devScale;
                float deviationY = (y - midPointY) * devScale;

                // No antialiasing
                // Ray ray = new Ray(new Vector(0,0,0), new Vector(deviationX, deviationY, 10), objects, 3);

                // Super sampling anti aliasing
                screen[x][y] = superSample(devScale, 0F, new Vector(deviationX, deviationY, 10), objects, ambientReflection, specularInt, 2);

            }

        }

        return getImage(screen);

    }

    private Image getImage(Vector[][] screen) {

        //Find the width and height of the image to be process
        int width = screen.length;
        int height = screen[0].length;
        //Create a new image of that width and height
        WritableImage newImage = new WritableImage(width, height);
        //Get an interface to write to that image memory
        PixelWriter image_writer = newImage.getPixelWriter();

        //Iterate over all pixels
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                image_writer.setColor(x, y, Color.color(screen[x][y].getX(), screen[x][y].getY(),screen[x][y].getZ()));
            }
        }

        return newImage;

    }

    // //Saves an image with a string path
    // private void saveImage(Image image, String path) {
    //     try {
    //         //Convert image to a buffered image
    //         BufferedImage sourceImage = SwingFXUtils.fromFXImage(image, null);

    //         //converting image, so it has 3 channels and no 4th channel! Important for saving
    //         BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(),
    //                 BufferedImage.TYPE_INT_RGB);
    //         newImage.getGraphics().drawImage(sourceImage, 0, 0, null);

    //         //Save converted image
    //         ImageIO.write(newImage, "jpg", new File(path));
    //     } catch (IOException e) {
    //         System.out.println("IO exception");
    //     }
    // }

    private Vector superSample(float devScale, float t, Vector baseDir, RenderObject[] objects, Vector ambientReflection, float specularInt, int depth) {

        // Split up the pixel into 4 sub pixels
        devScale/=4;
        Ray ray1 = new Ray(new Vector(0,0,0), new Vector(baseDir.getX() -(devScale), baseDir.getY() -(devScale), baseDir.getZ()), objects, depth);
        Ray ray2 = new Ray(new Vector(0,0,0), new Vector(baseDir.getX() +(devScale), baseDir.getY() -(devScale), baseDir.getZ()), objects, depth);
        Ray ray3 = new Ray(new Vector(0,0,0), new Vector(baseDir.getX() -(devScale), baseDir.getY() +(devScale), baseDir.getZ()), objects, depth);
        Ray ray4 = new Ray(new Vector(0,0,0), new Vector(baseDir.getX() +(devScale), baseDir.getY() +(devScale), baseDir.getZ()), objects, depth);
        // Calculate the rays
        Vector c1 = ray1.calculate(ambientReflection,specularInt,null);
        Vector c2 = ray2.calculate(ambientReflection,specularInt,null);
        Vector c3 = ray3.calculate(ambientReflection,specularInt,null);
        Vector c4 = ray4.calculate(ambientReflection,specularInt,null);
        
        // Sum up the vectors
        float sumC1 = Vector.sum(c1);
        float sumC2 = Vector.sum(c2);
        float sumC3 = Vector.sum(c3);
        float sumC4 = Vector.sum(c4);

        // Find the min and max sum
        float max = Math.max(sumC1, Math.max(sumC2, Math.max(sumC3, sumC4)));
        float min = Math.min(sumC1, Math.min(sumC2, Math.min(sumC3, sumC4)));

        // If the difference is smaller than some threshold
        if (max - min <= t) {
            // Return the average
            Vector avg = Vector.product(Vector.add(c1, Vector.add(c2, Vector.add(c3, c4))), 0.25F);
            return avg;
        } else {
            // If there is a large difference then recursively apply the supersampling to the subpixels
            Vector cc1 = superSample(devScale / 4, 0.5F, new Vector(baseDir.getX() -(devScale), baseDir.getY() -(devScale), 10),
                    objects, ambientReflection, specularInt, 2);
            Vector cc2 = superSample(devScale / 4, 0.5F, new Vector(baseDir.getX() +(devScale), baseDir.getY() -(devScale), 10),
                    objects, ambientReflection, specularInt, 2);
            Vector cc3 = superSample(devScale / 4, 0.5F, new Vector(baseDir.getX() -(devScale), baseDir.getY() +(devScale), 10),
                    objects, ambientReflection, specularInt, 2);
            Vector cc4 = superSample(devScale / 4, 0.5F, new Vector(baseDir.getX() +(devScale), baseDir.getY() +(devScale), 10),
                    objects, ambientReflection, specularInt, 2);
            // Return the average
            Vector avg = Vector.product(Vector.add(cc1, Vector.add(cc2, Vector.add(cc3, cc4))), 0.25F);
            return avg;
        }
    }

}
