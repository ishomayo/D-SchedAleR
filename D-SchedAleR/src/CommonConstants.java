import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class CommonConstants {
    // Define resource paths using a more robust method
    private static final String BASE_PATH;

    static {
        // Try to get the resources directory from the classpath
        URL resourceUrl = CommonConstants.class.getClassLoader().getResource("resources");
        
        if (resourceUrl != null) {
            // Use the URL path if available
            BASE_PATH = resourceUrl.getPath() + File.separator;
        } else {
            // Fallback to a relative path from the current working directory
            String currentDir = System.getProperty("user.dir");
            
            // Check if we're running from the project root or from within the src directory
            if (new File(currentDir + "/D-SchedAleR/src/resources").exists()) {
                BASE_PATH = "/D-SchedAleR/src/resources/";
            } else if (new File(currentDir + "/src/resources").exists()) {
                BASE_PATH = "/src/resources/";
            } else {
                // If none of the above paths work, try to use a resource folder in the current directory
                BASE_PATH = "/resources/";
                
                // Create the resources directory if it doesn't exist
                new File(BASE_PATH).mkdirs();
                
                System.out.println("Warning: Resource directory not found. Using " + 
                                  new File(BASE_PATH).getAbsolutePath() + " instead.");
            }
        }
        
        System.out.println("Using resource path: " + BASE_PATH);
    }

    // File paths using the determined BASE_PATH
    public static final String splashScreen = BASE_PATH + "splash.gif";
    public static final String lobbyBG = BASE_PATH + "lobby.jpg";

    public static final String startDefault = BASE_PATH + "start-default.png";
    public static final String startHover = BASE_PATH + "start-hover.png";
    public static final String startClick = BASE_PATH + "start-click.png";

    public static final String helpDefault = BASE_PATH + "help-default.png";
    public static final String helpHover = BASE_PATH + "help-hover.png";
    public static final String helpClick = BASE_PATH + "help-click.png";

    public static final String creditsDefault = BASE_PATH + "credits-default.png";
    public static final String creditsHover = BASE_PATH + "credits-hover.png";
    public static final String creditsClick = BASE_PATH + "credits-click.png";

    public static final String exitDefault = BASE_PATH + "exit-default.png";
    public static final String exitHover = BASE_PATH + "exit-hover.png";
    public static final String exitClick = BASE_PATH + "exit-click.png";


    // Utility method to load images properly
    public static Image loadImage(String path) {
        try {
            File imageFile = new File(path);
            if (!imageFile.exists()) {
                System.err.println("File not found: " + path);
                return null;
            }
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
            return null;
        }
    }
}