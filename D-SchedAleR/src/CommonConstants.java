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
    public static final String dataMethodBG = BASE_PATH + "generate-data.png";
    public static final String algoSelectBG = BASE_PATH + "algo-select.png";

    public static final String randomDataMethodBG = BASE_PATH + "random.png";
    public static final String randomGeneratedMethodBG = BASE_PATH + "random-generated.png";
    public static final String userDataMethodBG = BASE_PATH + "user-input.png";
    public static final String fileDataMethodBG = BASE_PATH + "file.png";

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

    public static final String randomDefault = BASE_PATH + "random-default.png";
    public static final String randomHover = BASE_PATH + "random-hover.png";
    public static final String randomClick = BASE_PATH + "random-click.png";

    public static final String userDefault = BASE_PATH + "user-default.png";
    public static final String userHover = BASE_PATH + "user-hover.png";
    public static final String userClick = BASE_PATH + "user-click.png";

    public static final String fileDefault = BASE_PATH + "file-default.png";
    public static final String fileHover = BASE_PATH + "file-hover.png";
    public static final String fileClick = BASE_PATH + "file-click.png";

    public static final String backDefault = BASE_PATH + "back-default.png";
    public static final String backHover = BASE_PATH + "back-hover.png";
    public static final String backClick = BASE_PATH + "back-click.png";

    public static final String generateDefault = BASE_PATH + "generate-default.png";
    public static final String generateHover = BASE_PATH + "generate-hover.png";
    public static final String generateClick = BASE_PATH + "generate-click.png";

    public static final String continueDefault = BASE_PATH + "continue-default.png";
    public static final String continueHover = BASE_PATH + "continue-hover.png";
    public static final String continueClick = BASE_PATH + "continue-click.png";

    public static final String fcfsDefault = BASE_PATH + "fcfs-default.png";
    public static final String fcfsHover = BASE_PATH + "fcfs-hover.png";
    public static final String fcfsClick = BASE_PATH + "fcfs-click.png";

    public static final String sstfDefault = BASE_PATH + "sstf-default.png";
    public static final String sstfHover = BASE_PATH + "sstf-hover.png";
    public static final String sstfClick = BASE_PATH + "sstf-click.png";

    public static final String scanDefault = BASE_PATH + "scan-default.png";
    public static final String scanHover = BASE_PATH + "scan-hover.png";
    public static final String scanClick = BASE_PATH + "scan-click.png";
    
    public static final String cscanDefault = BASE_PATH + "cscan-default.png";
    public static final String cscanHover = BASE_PATH + "cscan-hover.png";
    public static final String cscanClick = BASE_PATH + "cscan-click.png";

    public static final String lookDefault = BASE_PATH + "look-default.png";
    public static final String lookHover = BASE_PATH + "look-hover.png";    
    public static final String lookClick = BASE_PATH + "look-click.png";

    public static final String clookDefault = BASE_PATH + "clook-default.png";
    public static final String clookHover = BASE_PATH + "clook-hover.png";
    public static final String clookClick = BASE_PATH + "clook-click.png";

    public static final String simulateALLDefault = BASE_PATH + "simulateALL-default.png";
    public static final String simulateALLHover = BASE_PATH + "simulateALL-hover.png";
    public static final String simulateALLClick = BASE_PATH + "simulateALL-click.png";

    public static final String startDefaultSim = BASE_PATH + "start_default.png";
    public static final String startHoverSim = BASE_PATH + "start_hover.png";

    public static final String backDefaultSim = BASE_PATH + "back2_default.png";
    public static final String backHoverSim = BASE_PATH + "back2_hover.png";

    public static final String simulation_screen_FCFS = BASE_PATH + "simulation_screen_FCFS.png";

    public static final String simulation_screen_SSTF = BASE_PATH + "simulation_screen_SSTF.png";

    public static final String simulation_screen_SCAN = BASE_PATH + "simulation_screen_SCAN.png";

    public static final String simulation_screen_CLOOK = BASE_PATH + "simulation_screen_CLOOK.png";

    public static final String simulation_screen_CSCAN = BASE_PATH + "simulation_screen_CSCAN.png";

    public static final String simulation_screen_LOOK = BASE_PATH + "simulation_screen_LOOK.png";
    
    

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