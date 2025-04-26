import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

public class CommonConstants {
    // Define resource paths - using the format that worked for lobby.jpg
    private static final String RESOURCE_PREFIX = "resources/";
    
    // File paths
    public static final String splashScreen = RESOURCE_PREFIX + "splash.gif";
    public static final String lobbyBG = RESOURCE_PREFIX + "lobby.jpg";
    public static final String dataMethodBG = RESOURCE_PREFIX + "generate-data.png";
    public static final String algoSelectBG = RESOURCE_PREFIX + "algo-select.png";
    public static final String help = RESOURCE_PREFIX + "help.png";
    public static final String credits = RESOURCE_PREFIX + "creds.png";

    public static final String randomDataMethodBG = RESOURCE_PREFIX + "random.png";
    public static final String randomGeneratedMethodBG = RESOURCE_PREFIX + "random-generated.png";

    public static final String userDataMethodBG = RESOURCE_PREFIX + "user-input.png";

    public static final String fileMethodBG = RESOURCE_PREFIX + "file-input.png";
    public static final String fileUploadedMethodBG = RESOURCE_PREFIX + "file-input-uploaded.png";

    public static final String startDefault = RESOURCE_PREFIX + "start-default.png";
    public static final String startHover = RESOURCE_PREFIX + "start-hover.png";
    public static final String startClick = RESOURCE_PREFIX + "start-click.png";

    public static final String helpDefault = RESOURCE_PREFIX + "help-default.png";
    public static final String helpHover = RESOURCE_PREFIX + "help-hover.png";
    public static final String helpClick = RESOURCE_PREFIX + "help-click.png";

    public static final String creditsDefault = RESOURCE_PREFIX + "credits-default.png";
    public static final String creditsHover = RESOURCE_PREFIX + "credits-hover.png";
    public static final String creditsClick = RESOURCE_PREFIX + "credits-click.png";

    public static final String exitDefault = RESOURCE_PREFIX + "exit-default.png";
    public static final String exitHover = RESOURCE_PREFIX + "exit-hover.png";
    public static final String exitClick = RESOURCE_PREFIX + "exit-click.png";

    public static final String randomDefault = RESOURCE_PREFIX + "random-default.png";
    public static final String randomHover = RESOURCE_PREFIX + "random-hover.png";
    public static final String randomClick = RESOURCE_PREFIX + "random-click.png";

    public static final String userDefault = RESOURCE_PREFIX + "user-default.png";
    public static final String userHover = RESOURCE_PREFIX + "user-hover.png";
    public static final String userClick = RESOURCE_PREFIX + "user-click.png";

    public static final String fileDefault = RESOURCE_PREFIX + "file-default.png";
    public static final String fileHover = RESOURCE_PREFIX + "file-hover.png";
    public static final String fileClick = RESOURCE_PREFIX + "file-click.png";

    public static final String backDefault = RESOURCE_PREFIX + "back-default.png";
    public static final String backHover = RESOURCE_PREFIX + "back-hover.png";
    public static final String backClick = RESOURCE_PREFIX + "back-click.png";

    public static final String generateDefault = RESOURCE_PREFIX + "generate-default.png";
    public static final String generateHover = RESOURCE_PREFIX + "generate-hover.png";
    public static final String generateClick = RESOURCE_PREFIX + "generate-click.png";

    public static final String uploadDefault = RESOURCE_PREFIX + "upload-default.png";
    public static final String uploadHover = RESOURCE_PREFIX + "upload-hover.png";
    public static final String uploadClick = RESOURCE_PREFIX + "upload-click.png";

    public static final String continueDefault = RESOURCE_PREFIX + "continue-default.png";
    public static final String continueHover = RESOURCE_PREFIX + "continue-hover.png";
    public static final String continueClick = RESOURCE_PREFIX + "continue-click.png";

    public static final String fcfsDefault = RESOURCE_PREFIX + "fcfs-default.png";
    public static final String fcfsHover = RESOURCE_PREFIX + "fcfs-hover.png";
    public static final String fcfsClick = RESOURCE_PREFIX + "fcfs-click.png";

    public static final String sstfDefault = RESOURCE_PREFIX + "sstf-default.png";
    public static final String sstfHover = RESOURCE_PREFIX + "sstf-hover.png";
    public static final String sstfClick = RESOURCE_PREFIX + "sstf-click.png";

    public static final String scanDefault = RESOURCE_PREFIX + "scan-default.png";
    public static final String scanHover = RESOURCE_PREFIX + "scan-hover.png";
    public static final String scanClick = RESOURCE_PREFIX + "scan-click.png";
    
    public static final String cscanDefault = RESOURCE_PREFIX + "cscan-default.png";
    public static final String cscanHover = RESOURCE_PREFIX + "cscan-hover.png";
    public static final String cscanClick = RESOURCE_PREFIX + "cscan-click.png";

    public static final String lookDefault = RESOURCE_PREFIX + "look-default.png";
    public static final String lookHover = RESOURCE_PREFIX + "look-hover.png";    
    public static final String lookClick = RESOURCE_PREFIX + "look-click.png";

    public static final String clookDefault = RESOURCE_PREFIX + "clook-default.png";
    public static final String clookHover = RESOURCE_PREFIX + "clook-hover.png";
    public static final String clookClick = RESOURCE_PREFIX + "clook-click.png";

    public static final String simulateALLDefault = RESOURCE_PREFIX + "simulateALL-default.png";
    public static final String simulateALLHover = RESOURCE_PREFIX + "simulateALL-hover.png";
    public static final String simulateALLClick = RESOURCE_PREFIX + "simulateALL-click.png";

    public static final String startDefaultSim = RESOURCE_PREFIX + "start_default.png";
    public static final String startHoverSim = RESOURCE_PREFIX + "start_hover.png";

    public static final String backDefaultSim = RESOURCE_PREFIX + "back2_default.png";
    public static final String backHoverSim = RESOURCE_PREFIX + "back2_hover.png";

    public static final String simulation_screen_FCFS = RESOURCE_PREFIX + "simulation_screen_FCFS.png";
    public static final String simulation_screen_SSTF = RESOURCE_PREFIX + "simulation_screen_SSTF.png";
    public static final String simulation_screen_SCAN = RESOURCE_PREFIX + "simulation_screen_SCAN.png";
    public static final String simulation_screen_CLOOK = RESOURCE_PREFIX + "simulation_screen_CLOOK.png";
    public static final String simulation_screen_CSCAN = RESOURCE_PREFIX + "simulation_screen_CSCAN.png";
    public static final String simulation_screen_LOOK = RESOURCE_PREFIX + "simulation_screen_LOOK.png";
    public static final String simulation_screen_all = RESOURCE_PREFIX + "simulation_screen_all.png";

    /**
     * Loads an image resource using ClassLoader.getSystemResourceAsStream
     * which worked for at least some resources like lobby.jpg
     * 
     * @param resourcePath Path to the resource
     * @return The loaded Image or null if it couldn't be loaded
     */
    public static Image loadImage(String resourcePath) {
        try {
            // First try the method that worked for lobby.jpg
            InputStream is = ClassLoader.getSystemResourceAsStream(resourcePath);
            
            // If that fails, try with the class's class loader
            if (is == null) {
                is = CommonConstants.class.getClassLoader().getResourceAsStream(resourcePath);
            }
            
            // If that fails too, try without the resources/ prefix
            if (is == null && resourcePath.startsWith(RESOURCE_PREFIX)) {
                String altPath = resourcePath.substring(RESOURCE_PREFIX.length());
                is = ClassLoader.getSystemResourceAsStream(altPath);
                
                if (is == null) {
                    is = CommonConstants.class.getClassLoader().getResourceAsStream(altPath);
                }
            }
            
            // If all methods fail, print an error and return null
            if (is == null) {
                System.err.println("Could not find resource: " + resourcePath);
                return null;
            }
            
            // Read the image from the input stream
            Image img = ImageIO.read(is);
            is.close();
            
            if (img == null) {
                System.err.println("Failed to decode image: " + resourcePath);
                return null;
            }
            
            return img;
        } catch (IOException e) {
            System.err.println("Error loading image '" + resourcePath + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Utility method to create an ImageIcon from a resource path
     * @param resourcePath Path to the resource
     * @return The ImageIcon or null if resource couldn't be loaded
     */
    public static ImageIcon createImageIcon(String resourcePath) {
        Image img = loadImage(resourcePath);
        if (img != null) {
            return new ImageIcon(img);
        }
        return new ImageIcon(img);
    }
}