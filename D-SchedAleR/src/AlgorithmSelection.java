import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class AlgorithmSelection extends JPanel {

    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    private Image backgroundImage;
    private int width, height;
    private DataMethod dataMethod; // Add reference to DataMethod

    public AlgorithmSelection(Main main, CardLayout layout, JPanel mainPanel, int width, int height) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.algoSelectBG);

        JButton backButton = createStyledButton(CommonConstants.backDefault,
                CommonConstants.backHover, CommonConstants.backClick, new Dimension(220, 56));
        backButton.setBounds(37, 625, 220, 56);
        add(backButton);
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        JButton fcfsButton = createStyledButton(CommonConstants.fcfsDefault,
                CommonConstants.fcfsHover, CommonConstants.fcfsClick, new Dimension(283, 207));
        fcfsButton.setBounds(312, 170, 283, 207);
        add(fcfsButton);
        fcfsButton.addActionListener(e -> {
            // Get data from DataMethod panel
            DataMethod dataMethod = main.getDataMethod();

            // Print debug information
            System.out.println("FCFS button clicked, dataMethod is: " + (dataMethod != null ? "available" : "NULL"));

            // Use sample data if dataMethod is null
            if (dataMethod == null) {
                System.out.println("Using sample data instead");
                // Create sample data
                java.util.List<Integer> sampleQueue = new java.util.ArrayList<>();
                sampleQueue.add(82);
                sampleQueue.add(170);
                sampleQueue.add(43);
                sampleQueue.add(140);
                sampleQueue.add(24);
                sampleQueue.add(16);
                sampleQueue.add(190);

                FCFSSimulation fcfsSimulation = new FCFSSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        sampleQueue,
                        43, // Head start position from image
                        "RIGHT" // Default direction
                );
                mainPanel.add(fcfsSimulation, "FCFSSimulation");
                layout.show(mainPanel, "FCFSSimulation");
            } else {
                // Use actual data from DataMethod
                FCFSSimulation fcfsSimulation = new FCFSSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        dataMethod.getRequestQueue(),
                        dataMethod.getHeadStart(),
                        dataMethod.getDirection());
                mainPanel.add(fcfsSimulation, "FCFSSimulation");
                layout.show(mainPanel, "FCFSSimulation");
            }
        });

        JButton sstfButton = createStyledButton(CommonConstants.sstfDefault,
                CommonConstants.sstfHover, CommonConstants.sstfClick, new Dimension(283, 207));
        sstfButton.setBounds(628, 170, 283, 207);
        add(sstfButton);

        sstfButton.addActionListener(e -> {
            // Get data from DataMethod panel
            DataMethod dataMethod = main.getDataMethod();

            // Print debug information
            System.out.println("SSTF button clicked, dataMethod is: " + (dataMethod != null ? "available" : "NULL"));

            // Use sample data if dataMethod is null
            if (dataMethod == null) {
                System.out.println("Using sample data instead");
                // Create sample data
                java.util.List<Integer> sampleQueue = new java.util.ArrayList<>();
                sampleQueue.add(82);
                sampleQueue.add(170);
                sampleQueue.add(43);
                sampleQueue.add(140);
                sampleQueue.add(24);
                sampleQueue.add(16);
                sampleQueue.add(190);

                SSTFSimulation SSTFSimulation = new SSTFSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        sampleQueue,
                        43, // Head start position from image
                        "RIGHT" // Default direction
                );
                mainPanel.add(SSTFSimulation, "SSTFSimulation");
                layout.show(mainPanel, "SSTFSimulation");
            } else {
                // Use actual data from DataMethod
                SSTFSimulation SSTFSimulation = new SSTFSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        dataMethod.getRequestQueue(),
                        dataMethod.getHeadStart(),
                        dataMethod.getDirection());
                mainPanel.add(SSTFSimulation, "SSTFSimulation");
                layout.show(mainPanel, "SSTFSimulation");
            }
        });

        JButton scanButton = createStyledButton(CommonConstants.scanDefault,
                CommonConstants.scanHover, CommonConstants.scanClick, new Dimension(283, 207));
        scanButton.setBounds(944, 170, 283, 207);
        add(scanButton);

        scanButton.addActionListener(e -> {
            // Get data from DataMethod panel
            DataMethod dataMethod = main.getDataMethod();

            // Print debug information
            System.out.println("SSTF button clicked, dataMethod is: " + (dataMethod != null ? "available" : "NULL"));

            // Use sample data if dataMethod is null
            if (dataMethod == null) {
                System.out.println("Using sample data instead");
                // Create sample data
                java.util.List<Integer> sampleQueue = new java.util.ArrayList<>();
                sampleQueue.add(82);
                sampleQueue.add(170);
                sampleQueue.add(43);
                sampleQueue.add(140);
                sampleQueue.add(24);
                sampleQueue.add(16);
                sampleQueue.add(190);

                SCANSimulation SCANSimulation = new SCANSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        sampleQueue,
                        43, // Head start position from image
                        "RIGHT" // Default direction
                );
                mainPanel.add(SCANSimulation, "SCANSimulation");
                layout.show(mainPanel, "SCANSimulation");
            } else {
                // Use actual data from DataMethod
                SCANSimulation SCANSimulation = new SCANSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        dataMethod.getRequestQueue(),
                        dataMethod.getHeadStart(),
                        dataMethod.getDirection());
                mainPanel.add(SCANSimulation, "SCANSimulation");
                layout.show(mainPanel, "SCANSimulation");
            }
        });

        JButton cscanButton = createStyledButton(CommonConstants.cscanDefault,
                CommonConstants.cscanHover, CommonConstants.cscanClick, new Dimension(283, 207));
        cscanButton.setBounds(312, 405, 283, 207);
        add(cscanButton);

        cscanButton.addActionListener(e -> {
            // Get data from DataMethod panel
            DataMethod dataMethod = main.getDataMethod();

            // Print debug information
            System.out.println("SSTF button clicked, dataMethod is: " + (dataMethod != null ? "available" : "NULL"));

            // Use sample data if dataMethod is null
            if (dataMethod == null) {
                System.out.println("Using sample data instead");
                // Create sample data
                java.util.List<Integer> sampleQueue = new java.util.ArrayList<>();
                sampleQueue.add(82);
                sampleQueue.add(170);
                sampleQueue.add(43);
                sampleQueue.add(140);
                sampleQueue.add(24);
                sampleQueue.add(16);
                sampleQueue.add(190);

                CSCANSimulation CSCANSimulation = new CSCANSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        sampleQueue,
                        43, // Head start position from image
                        "RIGHT" // Default direction
                );
                mainPanel.add(CSCANSimulation, "CSCANSimulation");
                layout.show(mainPanel, "CSCANSimulation");
            } else {
                // Use actual data from DataMethod
                CSCANSimulation CSCANSimulation = new CSCANSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        dataMethod.getRequestQueue(),
                        dataMethod.getHeadStart(),
                        dataMethod.getDirection());
                mainPanel.add(CSCANSimulation, "CSCANSimulation");
                layout.show(mainPanel, "CSCANSimulation");
            }
        });

        JButton lookButton = createStyledButton(CommonConstants.lookDefault,
                CommonConstants.lookHover, CommonConstants.lookClick, new Dimension(283, 207));
        lookButton.setBounds(628, 405, 283, 207);
        add(lookButton);

        lookButton.addActionListener(e -> {
            // Get data from DataMethod panel
            DataMethod dataMethod = main.getDataMethod();

            // Print debug information
            System.out.println("SSTF button clicked, dataMethod is: " + (dataMethod != null ? "available" : "NULL"));

            // Use sample data if dataMethod is null
            if (dataMethod == null) {
                System.out.println("Using sample data instead");
                // Create sample data
                java.util.List<Integer> sampleQueue = new java.util.ArrayList<>();
                sampleQueue.add(82);
                sampleQueue.add(170);
                sampleQueue.add(43);
                sampleQueue.add(140);
                sampleQueue.add(24);
                sampleQueue.add(16);
                sampleQueue.add(190);

                LOOKSimulation LOOKSimulation = new LOOKSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        sampleQueue,
                        43, // Head start position from image
                        "RIGHT" // Default direction
                );
                mainPanel.add(LOOKSimulation, "LOOKSimulation");
                layout.show(mainPanel, "LOOKSimulation");
            } else {
                // Use actual data from DataMethod
                LOOKSimulation LOOKSimulation = new LOOKSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        dataMethod.getRequestQueue(),
                        dataMethod.getHeadStart(),
                        dataMethod.getDirection());
                mainPanel.add(LOOKSimulation, "LOOKSimulation");
                layout.show(mainPanel, "LOOKSimulation");
            }
        });

        JButton clookButton = createStyledButton(CommonConstants.clookDefault,
                CommonConstants.clookHover, CommonConstants.clookClick, new Dimension(283, 207));
        clookButton.setBounds(944, 405, 283, 207);
        add(clookButton);

        clookButton.addActionListener(e -> {
            // Get data from DataMethod panel
            DataMethod dataMethod = main.getDataMethod();

            // Print debug information
            System.out.println("SSTF button clicked, dataMethod is: " + (dataMethod != null ? "available" : "NULL"));

            // Use sample data if dataMethod is null
            if (dataMethod == null) {
                System.out.println("Using sample data instead");
                // Create sample data
                java.util.List<Integer> sampleQueue = new java.util.ArrayList<>();
                sampleQueue.add(82);
                sampleQueue.add(170);
                sampleQueue.add(43);
                sampleQueue.add(140);
                sampleQueue.add(24);
                sampleQueue.add(16);
                sampleQueue.add(190);

                CLOOKSimulation CLOOKSimulation = new CLOOKSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        sampleQueue,
                        43, // Head start position from image
                        "RIGHT" // Default direction
                );
                mainPanel.add(CLOOKSimulation, "CLOOKSimulation");
                layout.show(mainPanel, "CLOOKSimulation");
            } else {
                // Use actual data from DataMethod
                CLOOKSimulation CLOOKSimulation = new CLOOKSimulation(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        dataMethod.getRequestQueue(),
                        dataMethod.getHeadStart(),
                        dataMethod.getDirection());
                mainPanel.add(CLOOKSimulation, "CLOOKSimulation");
                layout.show(mainPanel, "CLOOKSimulation");
            }
        });

        JButton simulateALLButton = createStyledButton(CommonConstants.simulateALLDefault,
                CommonConstants.simulateALLHover, CommonConstants.simulateALLClick, new Dimension(220, 56));
        simulateALLButton.setBounds(660, 610, 220, 70);
        add(simulateALLButton);

        simulateALLButton.addActionListener(e -> {
            // Get data from DataMethod panel
            DataMethod dataMethod = main.getDataMethod();

            // Print debug information
            System.out.println("SSTF button clicked, dataMethod is: " + (dataMethod != null ? "available" : "NULL"));

            // Use sample data if dataMethod is null
            if (dataMethod == null) {
                System.out.println("Using sample data instead");
                // Create sample data
                java.util.List<Integer> sampleQueue = new java.util.ArrayList<>();
                sampleQueue.add(82);
                sampleQueue.add(170);
                sampleQueue.add(43);
                sampleQueue.add(140);
                sampleQueue.add(24);
                sampleQueue.add(16);
                sampleQueue.add(190);

                CombinedSimulationScreen CombinedSimulationScreen = new CombinedSimulationScreen(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        sampleQueue,
                        43, // Head start position from image
                        "RIGHT" // Default direction
                );
                mainPanel.add(CombinedSimulationScreen, "CombinedSimulationScreen");
                layout.show(mainPanel, "CombinedSimulationScreen");
            } else {
                // Use actual data from DataMethod
                CombinedSimulationScreen CombinedSimulationScreen = new CombinedSimulationScreen(
                        main,
                        layout,
                        mainPanel,
                        width,
                        height,
                        dataMethod.getRequestQueue(),
                        dataMethod.getHeadStart(),
                        dataMethod.getDirection());
                mainPanel.add(CombinedSimulationScreen, "CombinedSimulationScreen");
                layout.show(mainPanel, "CombinedSimulationScreen");
            }
        });
    }

    // Set the DataMethod reference
    public void setDataMethod(DataMethod dataMethod) {
        this.dataMethod = dataMethod;
    }

    // *******************************************************
    //
    // HELPER METHODS
    //
    // *******************************************************

    private static JButton createStyledButton(String defaultIconPath, String hoverIconPath, String clickIconPath,
            Dimension size) {
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(size);

        ImageIcon defaultIcon = scaleImage(defaultIconPath, size);
        ImageIcon hoverIcon = scaleImage(hoverIconPath, size);
        ImageIcon clickIcon = scaleImage(clickIconPath, size);

        button.setIcon(defaultIcon);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(defaultIcon);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setIcon(clickIcon);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setIcon(hoverIcon);
            }
        });

        return button;
    }

    private static ImageIcon scaleImage(String path, Dimension size) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}