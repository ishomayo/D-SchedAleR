import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// Import utility classes
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.io.IOException;
import java.text.SimpleDateFormat;

// Import iText PDF library
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

public class CombinedSimulationScreen extends JPanel {
    private JPanel mainPanel;
    private CardLayout layout;
    private Main main;
    private Image backgroundImage;
    private JButton startButton, restartButton, pauseButton, saveButton;
    private int width, height;
    private List<Integer> requestQueue;
    private int headStart;
    private String direction;
    private JSlider speedSlider;
    private Timer animationTimer;
    private boolean isAnimating = false;
    private int currentStep = 0;
    private JLabel timerLabel;
    private long startTime;
    private long elapsedTime = 0;
    private Timer displayTimer;

    // Components for the scrollable content
    private JScrollPane scrollPane;
    private JPanel contentPanel;
    private JPanel scrollablePanel;

    // Store algorithm panels and their data
    private Map<String, AlgorithmPanel> algorithmPanels;
    private String[] algorithms = { "FCFS", "SSTF", "SCAN", "C-SCAN", "LOOK", "C-LOOK" };
    private final int MAX_DISK_SIZE = 199;

    public CombinedSimulationScreen(Main main, CardLayout layout, JPanel mainPanel, int width, int height,
            List<Integer> requestQueue, int headStart, String direction) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.width = width;
        this.height = height;
        this.requestQueue = requestQueue;
        this.headStart = headStart;
        this.direction = direction;

        setSize(width, height);
        setLayout(null);
        backgroundImage = CommonConstants.loadImage(CommonConstants.simulation_screen_all);

        algorithmPanels = new HashMap<>();

        createUI();
        calculateAllAlgorithms();
    }

    private void createUI() {
        // Title panel at the top
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBounds(0, 0, width, 50);
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(" ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel);

        // Back button
        JButton backButton = createStyledButton(CommonConstants.backDefaultSim,
                CommonConstants.backHoverSim, CommonConstants.backHoverSim, new Dimension(50, 50));
        backButton.setBounds(width - 120, 41, 50, 50);
        add(backButton);
        backButton.addActionListener(e -> layout.show(mainPanel, "AlgorithmSelection"));

        // Main content panel with red border
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBounds(60, 125, width - 120, 510);
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel);

        // Create the scrollable panel that will contain all algorithm visualizations
        scrollablePanel = new JPanel();
        scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
        scrollablePanel.setBackground(Color.WHITE);

        // Add all algorithm panels
        for (String algoName : algorithms) {
            AlgorithmPanel algoPanel = new AlgorithmPanel(algoName);
            algorithmPanels.put(algoName, algoPanel);
            scrollablePanel.add(algoPanel);

            // Add a small separator between algorithm panels
            if (!algoName.equals(algorithms[algorithms.length - 1])) {
                JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
                separator.setPreferredSize(new Dimension(width - 140, 2));
                separator.setMaximumSize(new Dimension(width - 140, 2));
                separator.setForeground(new Color(200, 200, 200));
                scrollablePanel.add(separator);

                // Add a bit of spacing
                scrollablePanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // Create the scroll pane and add the scrollable panel to it
        scrollPane = new JScrollPane(scrollablePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        // Apply the custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        // Make the scrollbar wider if needed
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Timer panel
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timerPanel.setOpaque(false);
        timerPanel.setBounds(80, 650, 90, 50);

        JLabel timerTitleLabel = new JLabel("Timer");
        timerTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerPanel.add(timerTitleLabel);

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        timerPanel.add(timerLabel);

        add(timerPanel);

        // Speed slider - using custom styled slider
        JLabel speedLabel = new JLabel("SPEED");
        speedLabel.setBounds(170, 650, 60, 25);
        add(speedLabel);

        // Create custom slider
        speedSlider = createCustomSlider(0, 100, 50);
        speedSlider.setBounds(230, 650, 300, 30);
        add(speedSlider);

        // Label for minimum value (Slow)
        JLabel slowLabel = new JLabel("Slow");
        slowLabel.setBounds(230, 680, 40, 20);
        add(slowLabel);

        // Label for maximum value (Fast)
        JLabel fastLabel = new JLabel("Fast");
        fastLabel.setBounds(530, 680, 40, 20);
        add(fastLabel);

        startButton = createOblongButton("START ALL", 150, 40);
        startButton.setBounds(580, 650, 150, 40);
        add(startButton);
        startButton.addActionListener(e -> startAnimation());

        restartButton = createOblongButton("RESTART ALL", 150, 40);
        restartButton.setBounds(750, 650, 150, 40);
        add(restartButton);
        restartButton.addActionListener(e -> restartAnimation());

        pauseButton = createOblongButton("PAUSE ALL", 150, 40);
        pauseButton.setBounds(920, 650, 150, 40);
        pauseButton.setEnabled(false); // Initially disabled until animation starts
        add(pauseButton);
        pauseButton.addActionListener(e -> pauseAnimation());

        saveButton = createOblongButton("SAVE", 150, 40);
        saveButton.setBounds(1085, 650, 150, 40);
        saveButton.setEnabled(false); // Initially disabled
        add(saveButton);
        saveButton.addActionListener(e -> showSaveDialog());
    }

    private void calculateAllAlgorithms() {
        // Calculate the results for each algorithm
        calculateFCFS();
        calculateSSTF();
        calculateSCAN();
        calculateCSCAN();
        calculateLOOK();
        calculateCLOOK();
    }

    private void calculateFCFS() {
        AlgorithmPanel panel = algorithmPanels.get("FCFS");
        int totalSeekTime = 0;
        int totalHeadMovements = requestQueue.size();
        int currentPosition = headStart;

        // Create a list of positions in FCFS order
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position

        StringBuilder sequence = new StringBuilder();

        for (Integer request : requestQueue) {
            positions.add(request);
            sequence.append(request).append(", ");
            totalSeekTime += Math.abs(currentPosition - request);
            currentPosition = request;
        }

        // Update fields
        if (sequence.length() > 2) {
            sequence.delete(sequence.length() - 2, sequence.length());
        }

        panel.setOrderSequence(sequence.toString());
        panel.setTotalSeekTime(totalSeekTime);
        panel.setTotalHeadMovements(totalHeadMovements);
        panel.setPositions(positions);
        panel.updateGraph();
    }

    private void calculateSSTF() {
        AlgorithmPanel panel = algorithmPanels.get("SSTF");
        int totalSeekTime = 0;
        int totalHeadMovements = requestQueue.size();
        int currentPosition = headStart;

        // Create a list to store the sequence of positions
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position

        StringBuilder sequence = new StringBuilder();

        // Create a copy of the request queue to work with
        List<Integer> remainingRequests = new ArrayList<>(requestQueue);

        // Process all requests
        while (!remainingRequests.isEmpty()) {
            // Find the closest request to the current head position
            int closestRequest = findClosestRequest(currentPosition, remainingRequests);

            // Add the closest request to the sequence
            positions.add(closestRequest);
            sequence.append(closestRequest).append(", ");

            // Update total seek time
            totalSeekTime += Math.abs(currentPosition - closestRequest);

            // Move head to this position
            currentPosition = closestRequest;

            // Remove this request from the remaining list
            remainingRequests.remove(Integer.valueOf(closestRequest));
        }

        // Update fields
        if (sequence.length() > 2) {
            sequence.delete(sequence.length() - 2, sequence.length());
        }

        panel.setOrderSequence(sequence.toString());
        panel.setTotalSeekTime(totalSeekTime);
        panel.setTotalHeadMovements(totalHeadMovements);
        panel.setPositions(positions);
        panel.updateGraph();
    }

    private void calculateSCAN() {
        AlgorithmPanel panel = algorithmPanels.get("SCAN");
        int totalSeekTime = 0;
        int totalHeadMovements = 0;
        int currentPosition = headStart;

        // Create a list to store the sequence of positions
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position

        StringBuilder sequence = new StringBuilder();

        // Create a copy of the request queue
        List<Integer> sortedRequests = new ArrayList<>(requestQueue);

        // Sort all requests in ascending order
        Collections.sort(sortedRequests);

        // Find requests that are greater than or equal to current position
        int index = 0;
        while (index < sortedRequests.size() && sortedRequests.get(index) < currentPosition) {
            index++;
        }

        // Determine the initial direction
        boolean movingTowardsLarger = "right".equalsIgnoreCase(direction) ||
                (!"left".equalsIgnoreCase(direction) && index < sortedRequests.size());

        // First phase: Handle requests in the initial direction and move to the disk
        // end
        if (movingTowardsLarger) {
            // Moving towards larger cylinder numbers
            for (int i = index; i < sortedRequests.size(); i++) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // Go to the end of the disk
            if (currentPosition < MAX_DISK_SIZE) {
                positions.add(MAX_DISK_SIZE);
                sequence.append(MAX_DISK_SIZE).append(", ");
                totalSeekTime += Math.abs(currentPosition - MAX_DISK_SIZE);
                currentPosition = MAX_DISK_SIZE;
                totalHeadMovements++;
            }

            // Second phase: Handle remaining requests in reverse direction
            for (int i = sortedRequests.size() - 1; i >= 0; i--) {
                int request = sortedRequests.get(i);
                if (request < currentPosition && !positions.contains(request)) {
                    positions.add(request);
                    sequence.append(request).append(", ");
                    totalSeekTime += Math.abs(currentPosition - request);
                    currentPosition = request;
                    totalHeadMovements++;
                }
            }
        } else {
            // Moving towards smaller cylinder numbers
            for (int i = index - 1; i >= 0; i--) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // Go to the beginning of the disk
            if (currentPosition > 0) {
                positions.add(0);
                sequence.append(0).append(", ");
                totalSeekTime += Math.abs(currentPosition - 0);
                currentPosition = 0;
                totalHeadMovements++;
            }

            // Second phase: Handle remaining requests in reverse direction
            for (int i = 0; i < sortedRequests.size(); i++) {
                int request = sortedRequests.get(i);
                if (request > currentPosition && !positions.contains(request)) {
                    positions.add(request);
                    sequence.append(request).append(", ");
                    totalSeekTime += Math.abs(currentPosition - request);
                    currentPosition = request;
                    totalHeadMovements++;
                }
            }
        }

        // Update fields
        if (sequence.length() > 2) {
            sequence.delete(sequence.length() - 2, sequence.length());
        }

        panel.setOrderSequence(sequence.toString());
        panel.setTotalSeekTime(totalSeekTime);
        panel.setTotalHeadMovements(totalHeadMovements);
        panel.setPositions(positions);
        panel.updateGraph();
    }

    private void calculateCSCAN() {
        AlgorithmPanel panel = algorithmPanels.get("C-SCAN");
        int totalSeekTime = 0;
        int totalHeadMovements = 0;
        int currentPosition = headStart;

        // Create a list to store the sequence of positions
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position

        StringBuilder sequence = new StringBuilder();

        // Create a copy of the request queue
        List<Integer> sortedRequests = new ArrayList<>(requestQueue);

        // Sort all requests in ascending order
        Collections.sort(sortedRequests);

        // Find the index where requests are greater than or equal to current position
        int index = 0;
        while (index < sortedRequests.size() && sortedRequests.get(index) < currentPosition) {
            index++;
        }

        // Determine direction (default to moving toward larger cylinder numbers)
        boolean movingTowardsLarger = "right".equalsIgnoreCase(direction) ||
                (!"left".equalsIgnoreCase(direction));

        if (movingTowardsLarger) {
            // Moving towards larger cylinder numbers first

            // First service all requests >= current position
            for (int i = index; i < sortedRequests.size(); i++) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // Go to the end of the disk
            if (currentPosition < MAX_DISK_SIZE) {
                positions.add(MAX_DISK_SIZE);
                sequence.append(MAX_DISK_SIZE).append(", ");
                totalSeekTime += Math.abs(currentPosition - MAX_DISK_SIZE);
                currentPosition = MAX_DISK_SIZE;
                totalHeadMovements++;
            }

            // Quick return to the beginning (0) without servicing requests on the way
            positions.add(0);
            sequence.append("0").append(", ");
            totalSeekTime += MAX_DISK_SIZE;
            currentPosition = 0;
            totalHeadMovements++;

            // Service all requests from the beginning up to the starting position
            for (int i = 0; i < index; i++) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }
        } else {
            // Moving towards smaller cylinder numbers first

            // First service all requests < current position (in reverse order)
            for (int i = index - 1; i >= 0; i--) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // Go to the beginning of the disk
            if (currentPosition > 0) {
                positions.add(0);
                sequence.append("0").append(", ");
                totalSeekTime += Math.abs(currentPosition - 0);
                currentPosition = 0;
                totalHeadMovements++;
            }

            // Quick return to the end (MAX_DISK_SIZE) without servicing requests on the way
            positions.add(MAX_DISK_SIZE);
            sequence.append(MAX_DISK_SIZE).append(", ");
            totalSeekTime += MAX_DISK_SIZE;
            currentPosition = MAX_DISK_SIZE;
            totalHeadMovements++;

            // Service all requests from the end down to the starting position
            for (int i = sortedRequests.size() - 1; i >= index; i--) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }
        }

        // Update fields
        if (sequence.length() > 2) {
            sequence.delete(sequence.length() - 2, sequence.length());
        }

        panel.setOrderSequence(sequence.toString());
        panel.setTotalSeekTime(totalSeekTime);
        panel.setTotalHeadMovements(totalHeadMovements);
        panel.setPositions(positions);
        panel.updateGraph();
    }

    private void calculateLOOK() {
        AlgorithmPanel panel = algorithmPanels.get("LOOK");
        int totalSeekTime = 0;
        int totalHeadMovements = 0;
        int currentPosition = headStart;

        // Create a list to store the sequence of positions
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position

        StringBuilder sequence = new StringBuilder();

        // Create a copy of the request queue
        List<Integer> sortedRequests = new ArrayList<>(requestQueue);

        // Sort all requests in ascending order
        Collections.sort(sortedRequests);

        // Find the index where requests are greater than or equal to current position
        int index = 0;
        while (index < sortedRequests.size() && sortedRequests.get(index) < currentPosition) {
            index++;
        }

        // Determine the initial direction
        boolean movingTowardsLarger = "right".equalsIgnoreCase(direction) ||
                (!"left".equalsIgnoreCase(direction) && index < sortedRequests.size());

        // First phase: Handle requests in the initial direction
        if (movingTowardsLarger) {
            // Moving towards larger cylinder numbers
            for (int i = index; i < sortedRequests.size(); i++) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // Second phase: Handle the remaining requests in reverse direction
            for (int i = index - 1; i >= 0; i--) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }
        } else {
            // Moving towards smaller cylinder numbers
            for (int i = index - 1; i >= 0; i--) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // Second phase: Handle the remaining requests in reverse direction
            for (int i = index; i < sortedRequests.size(); i++) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }
        }

        // Update fields
        if (sequence.length() > 2) {
            sequence.delete(sequence.length() - 2, sequence.length());
        }

        panel.setOrderSequence(sequence.toString());
        panel.setTotalSeekTime(totalSeekTime);
        panel.setTotalHeadMovements(totalHeadMovements);
        panel.setPositions(positions);
        panel.updateGraph();
    }

    private void calculateCLOOK() {
        AlgorithmPanel panel = algorithmPanels.get("C-LOOK");
        int totalSeekTime = 0;
        int totalHeadMovements = 0;
        int currentPosition = headStart;

        // Create a list to store the sequence of positions
        List<Integer> positions = new ArrayList<>();
        positions.add(currentPosition); // Add starting position

        StringBuilder sequence = new StringBuilder();

        // Create a copy of the request queue
        List<Integer> sortedRequests = new ArrayList<>(requestQueue);

        // Sort all requests in ascending order
        Collections.sort(sortedRequests);

        // Find the index where requests are greater than or equal to current position
        int index = 0;
        while (index < sortedRequests.size() && sortedRequests.get(index) < currentPosition) {
            index++;
        }

        // Determine the initial direction (default to moving toward larger cylinder
        // numbers)
        boolean movingTowardsLarger = "right".equalsIgnoreCase(direction) ||
                (!"left".equalsIgnoreCase(direction));

        if (movingTowardsLarger) {
            // Moving towards larger cylinder numbers first

            // First service all requests >= current position
            for (int i = index; i < sortedRequests.size(); i++) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // If there are requests before the starting position, we need to "jump" to the
            // smallest request
            if (index > 0) {
                // Visualize the jump from the largest request to the smallest
                int smallestRequest = sortedRequests.get(0);

                // Add the jump to the visualization
                positions.add(smallestRequest);
                sequence.append(smallestRequest).append(", ");
                totalSeekTime += Math.abs(currentPosition - smallestRequest);
                currentPosition = smallestRequest;
                totalHeadMovements++;

                // Service all remaining requests (those < starting position, excluding the
                // smallest which we just did)
                for (int i = 1; i < index; i++) {
                    int request = sortedRequests.get(i);
                    positions.add(request);
                    sequence.append(request).append(", ");
                    totalSeekTime += Math.abs(currentPosition - request);
                    currentPosition = request;
                    totalHeadMovements++;
                }
            }
        } else {
            // Moving towards smaller cylinder numbers first

            // First service all requests < current position (in reverse order)
            for (int i = index - 1; i >= 0; i--) {
                int request = sortedRequests.get(i);
                positions.add(request);
                sequence.append(request).append(", ");
                totalSeekTime += Math.abs(currentPosition - request);
                currentPosition = request;
                totalHeadMovements++;
            }

            // If there are requests after the starting position, we need to "jump" to the
            // largest request
            if (index < sortedRequests.size()) {
                // Visualize the jump from the smallest request to the largest
                int largestRequest = sortedRequests.get(sortedRequests.size() - 1);

                // Add the jump to the visualization
                positions.add(largestRequest);
                sequence.append(largestRequest).append(", ");
                totalSeekTime += Math.abs(currentPosition - largestRequest);
                currentPosition = largestRequest;
                totalHeadMovements++;

                // Service all remaining requests (those >= starting position, excluding the
                // largest which we just did)
                for (int i = sortedRequests.size() - 2; i >= index; i--) {
                    int request = sortedRequests.get(i);
                    positions.add(request);
                    sequence.append(request).append(", ");
                    totalSeekTime += Math.abs(currentPosition - request);
                    currentPosition = request;
                    totalHeadMovements++;
                }
            }
        }

        // Update fields
        if (sequence.length() > 2) {
            sequence.delete(sequence.length() - 2, sequence.length());
        }

        panel.setOrderSequence(sequence.toString());
        panel.setTotalSeekTime(totalSeekTime);
        panel.setTotalHeadMovements(totalHeadMovements);
        panel.setPositions(positions);
        panel.updateGraph();
    }

    private int findClosestRequest(int currentPosition, List<Integer> requests) {
        int closestRequest = requests.get(0);
        int minDistance = Math.abs(currentPosition - closestRequest);

        for (int i = 1; i < requests.size(); i++) {
            int request = requests.get(i);
            int distance = Math.abs(currentPosition - request);

            if (distance < minDistance) {
                minDistance = distance;
                closestRequest = request;
            }
        }

        return closestRequest;
    }

    private void startAnimation() {
        // If already animating, don't do anything
        if (isAnimating)
            return;

        // Now we can start/resume the animation from the current step
        isAnimating = true;

        // Start or resume the timer
        if (currentStep == 0) {
            // Reset elapsed time for fresh start
            elapsedTime = 0;
            startTime = System.currentTimeMillis();
        } else {
            // For resuming after pause, capture current time
            startTime = System.currentTimeMillis();
        }

        // Create and start display timer for updating the timer label
        displayTimer = new Timer(100, e -> {
            if (isAnimating) {
                long currentTime = System.currentTimeMillis();
                long newElapsed = elapsedTime + (currentTime - startTime);
                updateTimerDisplay(newElapsed);
            }
        });
        displayTimer.start();

        // Update button states
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        restartButton.setEnabled(true);

        int delay = 505 - (speedSlider.getValue() * 5); // Convert 0-100 to 500-0 (faster at higher values)

        // Get maximum step count among all algorithms - make it final to access in
        // lambda
        final int maxSteps = getMaxSteps();

        animationTimer = new Timer(delay, e -> {
            if (currentStep < maxSteps) {
                currentStep++;

                // Update all algorithm panels with the new step
                for (AlgorithmPanel panel : algorithmPanels.values()) {
                    panel.setCurrentStep(Math.min(currentStep, panel.getMaxSteps()));
                    panel.updateGraph();
                }
            } else {
                // Reached the end
                pauseAnimation();

                // Stop and finalize the timer display
                if (displayTimer != null) {
                    displayTimer.stop();
                }

                // Disable start button, keep restart enabled
                startButton.setEnabled(false);
                restartButton.setEnabled(true);
            }
        });

        animationTimer.start();
    }

    // Helper method to get the maximum steps across all algorithms
    private int getMaxSteps() {
        int maxSteps = 0;
        for (AlgorithmPanel panel : algorithmPanels.values()) {
            maxSteps = Math.max(maxSteps, panel.getMaxSteps());
        }
        return maxSteps;
    }

    private void pauseAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        isAnimating = false;

        // Update elapsed time and stop the display timer
        if (displayTimer != null) {
            displayTimer.stop();
            long currentTime = System.currentTimeMillis();
            elapsedTime += (currentTime - startTime);
            updateTimerDisplay(elapsedTime);
        }

        // Update button states
        // Only enable start if not at the end
        final int maxSteps = getMaxSteps();

        if (currentStep < maxSteps) {
            startButton.setEnabled(true);
        } else {
            startButton.setEnabled(false);
        }

        pauseButton.setEnabled(false);
        saveButton.setEnabled(true);
        restartButton.setEnabled(true);
    }

    private void restartAnimation() {
        // First stop any ongoing animation
        pauseAnimation();

        // Reset to beginning
        currentStep = 0;

        // Reset all algorithm panels
        for (AlgorithmPanel panel : algorithmPanels.values()) {
            panel.setCurrentStep(0);
            panel.updateGraph();
        }

        // Reset timer
        elapsedTime = 0;
        updateTimerDisplay(0);

        // Reset button states
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        restartButton.setEnabled(true);
        saveButton.setEnabled(false);

        // Don't automatically start - user needs to press start
        isAnimating = false;
    }

    // Helper method to format and update the timer display
    private void updateTimerDisplay(long timeInMillis) {
        // Convert milliseconds to minutes and seconds
        long totalSeconds = timeInMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        // Format as MM:SS
        String timeString = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(timeString);
    }

    private void showSaveDialog() {
        // Create a dialog to let user choose between PDF and PNG
        String[] options = { "Save as PDF", "Save as PNG", "Cancel" };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose export format:",
                "Save Simulation",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            saveAsPDF();
        } else if (choice == 1) {
            saveAsPNG();
        }
        // If choice is 2 (Cancel) or dialog is closed, do nothing
    }

    private String generateTimestampFilename() {
        // Create a date formatter for the pattern "MMddyy_HHmmss"
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy_HHmmss");

        // Get current date/time and format it
        String timestamp = dateFormat.format(new Date());

        // Return the formatted string with _DS suffix
        return timestamp + "_DS";
    }

    private BufferedImage captureEntireContentPanel() {
        // Capture the entire scrollable content, not just what's visible
        JPanel contentToCapture = scrollablePanel;

        // Create a BufferedImage with the dimensions of the entire content
        BufferedImage image = new BufferedImage(
                contentToCapture.getWidth(),
                contentToCapture.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        // Paint the entire content onto the image
        contentToCapture.paint(image.getGraphics());

        return image;
    }

    private void saveAsPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF files (*.pdf)", "pdf"));

        // Set default file name
        String baseFilename = generateTimestampFilename();
        fileChooser.setSelectedFile(new File(baseFilename + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure filename ends with .pdf
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                // Capture the entire scrollable content
                BufferedImage fullCapture = captureEntireContentPanel();

                // Create PDF document - using A4 landscape for better fit
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Calculate available space in the document
                float documentWidth = document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin();
                float documentHeight = document.getPageSize().getHeight() - document.topMargin()
                        - document.bottomMargin();

                // Get dimensions of the full image
                int imageWidth = fullCapture.getWidth();
                int imageHeight = fullCapture.getHeight();

                // Calculate scaling factor based on width
                float scaleFactor = documentWidth / imageWidth;
                int scaledHeight = (int) (imageHeight * scaleFactor);

                // If the scaled image is taller than document height, we need multiple pages
                if (scaledHeight > documentHeight) {
                    // Calculate how many pixels of the original image can fit on one page
                    int pixelsPerPage = (int) (documentHeight / scaleFactor);

                    // Calculate number of pages needed
                    int totalPages = (int) Math.ceil((double) imageHeight / pixelsPerPage);

                    for (int page = 0; page < totalPages; page++) {
                        if (page > 0) {
                            document.newPage(); // Add a new page for subsequent sections
                        }

                        // Calculate the section of the image to use for this page
                        int y = page * pixelsPerPage;
                        int height = Math.min(pixelsPerPage, imageHeight - y);

                        if (height <= 0)
                            break; // Safety check

                        // Create a sub-image for this page
                        BufferedImage pageImage = fullCapture.getSubimage(0, y, imageWidth, height);

                        // Convert BufferedImage to bytes for iText
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(pageImage, "png", baos);
                        byte[] imageBytes = baos.toByteArray();

                        // Create iText image
                        com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(imageBytes);

                        // Scale the image to fit the width
                        pdfImage.scaleToFit(documentWidth, documentHeight);

                        // Center the image
                        pdfImage.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);

                        // Add image to document
                        document.add(pdfImage);
                    }
                } else {
                    // If the content fits on a single page, use the original approach
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(fullCapture, "png", baos);
                    byte[] imageBytes = baos.toByteArray();

                    com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(imageBytes);
                    pdfImage.scaleToFit(documentWidth, documentHeight);
                    pdfImage.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
                    document.add(pdfImage);
                }

                document.close();
                writer.close();

                JOptionPane.showMessageDialog(this,
                        "Content saved as PDF: " + fileToSave.getAbsolutePath(),
                        "Save Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving PDF: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void saveAsPNG() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PNG");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG images (*.png)", "png"));

        // Set default file name
        String baseFilename = generateTimestampFilename();
        fileChooser.setSelectedFile(new File(baseFilename + ".png"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Ensure filename ends with .png
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            try {
                // Capture the entire scrollable content
                BufferedImage fullCapture = captureEntireContentPanel();

                // Write the complete image to file
                ImageIO.write(fullCapture, "png", fileToSave);

                JOptionPane.showMessageDialog(this,
                        "Content saved as PNG: " + fileToSave.getAbsolutePath(),
                        "Save Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving PNG: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Creates a custom styled slider with transparent background
     */
    private JSlider createCustomSlider(int min, int max, int value) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, value) {
            @Override
            public void updateUI() {
                setUI(new CustomSliderUI(this));
                updateLabelUIs();
            }

            // Override to ensure the component is fully transparent
            @Override
            protected void paintComponent(Graphics g) {
                // Don't call super.paintComponent to avoid background painting
                // Only call UI's paint methods that we've customized
                if (getUI() != null) {
                    // Let the UI delegate paint
                    getUI().paint(g, this);
                }
            }
        };

        // Remove ticks and labels that come by default
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);

        // Make the slider transparent
        slider.setOpaque(false);
        slider.setBackground(new Color(0, 0, 0, 0)); // Fully transparent

        return slider;
    }

    /**
     * Custom UI for the slider component with transparent background
     */
    class CustomSliderUI extends javax.swing.plaf.basic.BasicSliderUI {
        public CustomSliderUI(JSlider slider) {
            super(slider);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            // Only paint the specific parts we want (track and thumb)
            paintTrack(g);
            paintThumb(g);
            // Deliberately omit painting other parts
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = slider.getWidth();
            int height = slider.getHeight();
            int trackHeight = 4;

            // Draw the track - black line
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(trackHeight));
            g2d.drawLine(0, height / 2, width, height / 2);
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Rectangle thumbBounds = thumbRect;

            // Create a red filled circle for the thumb
            g2d.setColor(Color.RED);
            int diameter = 16; // Size of the circular thumb
            int x = thumbBounds.x + (thumbBounds.width - diameter) / 2;
            int y = thumbBounds.y + (thumbBounds.height - diameter) / 2;
            g2d.fillOval(x, y, diameter, diameter);
        }

        @Override
        protected Dimension getThumbSize() {
            // Make the thumb area larger to make it easier to grab
            return new Dimension(20, 20);
        }
    }

    private JButton createOblongButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Choose background color based on button state
                if (!isEnabled()) {
                    // Distinctive gray for disabled state
                    g2.setColor(new Color(220, 220, 220));
                } else if (getModel().isPressed()) {
                    // Darker background when pressed
                    g2.setColor(new Color(200, 200, 200));
                } else if (getModel().isRollover()) {
                    // Slightly lighter background when hovered
                    g2.setColor(new Color(240, 240, 240));
                } else {
                    // Default background
                    g2.setColor(Color.WHITE);
                }

                // Draw rounded rectangle background
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, height, height);

                // Draw border (light gray for disabled)
                if (!isEnabled()) {
                    g2.setColor(new Color(180, 180, 180)); // Lighter border for disabled
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, height, height);

                // Text settings - ensure perfect centering
                Font buttonFont = new Font("Arial", Font.BOLD, 14);
                g2.setFont(buttonFont);

                FontMetrics metrics = g2.getFontMetrics(buttonFont);
                int textWidth = metrics.stringWidth(getText());
                int textHeight = metrics.getHeight();

                // Calculate exact center position
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + metrics.getAscent();

                // Draw text (gray for disabled)
                if (!isEnabled()) {
                    g2.setColor(new Color(150, 150, 150)); // Gray text for disabled
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            // Override getPreferredSize to ensure the button has the right dimensions
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }
        };

        // Set button properties
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        return button;
    }

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
        try {
            // Load the original image
            ImageIcon originalIcon = new ImageIcon(path);
            Image originalImage = originalIcon.getImage();

            // Create a new buffered image with transparency
            BufferedImage resizedImage = new BufferedImage(
                    size.width, size.height, BufferedImage.TYPE_INT_ARGB);

            // Get the graphics context of the new image
            Graphics2D g2d = resizedImage.createGraphics();

            // Set better rendering hints for higher quality
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the original image onto the new one
            g2d.drawImage(originalImage, 0, 0, size.width, size.height, null);

            // Clean up
            g2d.dispose();

            // Create and return a new ImageIcon from the resized image
            return new ImageIcon(resizedImage);
        } catch (Exception e) {
            System.err.println("Error scaling image: " + path);
            e.printStackTrace();
            // Return the original icon if there's an error
            return new ImageIcon(path);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    /**
     * Inner class for each algorithm panel in the scrollable area
     */
    class AlgorithmPanel extends JPanel {
        private String algorithmName;
        private JTextField orderSequenceField, totalSeekTimeField, totalHeadMovementsField;
        private GraphPanel graphPanel;
        private List<Integer> positions = new ArrayList<>();
        private int currentStep = 0;

        public AlgorithmPanel(String algorithmName) {
            this.algorithmName = algorithmName;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(true);
            setBackground(Color.WHITE);
            setMaximumSize(new Dimension(width - 140, 520)); // Limit height to maintain uniformity
            setPreferredSize(new Dimension(width - 140, 520));

            createUI();
        }

        private void createUI() {
            // Algorithm title
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            titlePanel.setOpaque(false);

            JLabel titleLabel = new JLabel(algorithmName + " Algorithm");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
            titlePanel.add(titleLabel);

            add(titlePanel);

            // Main content panel
            JPanel contentPanel = new JPanel(null);
            contentPanel.setPreferredSize(new Dimension(width - 150, 480)); // Increased height
            contentPanel.setMaximumSize(new Dimension(width - 150, 480)); // Increased height

            contentPanel.setOpaque(false);

            // Order of Sequence
            JLabel orderLabel = new JLabel("Order of Sequence");
            orderLabel.setBounds(10, 2, 150, 25); // Already at top position
            orderLabel.setFont(new Font("Arial", Font.BOLD, 14));
            contentPanel.add(orderLabel);

            orderSequenceField = new JTextField();
            orderSequenceField.setBounds(160, 2, 750, 30); // Also moved up to align with label
            orderSequenceField.setEditable(false);
            orderSequenceField.setBackground(new Color(230, 230, 230));
            orderSequenceField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
            contentPanel.add(orderSequenceField);

            // Metrics panel
            JPanel metricsPanel = new JPanel(null);
            metricsPanel.setBounds(10, 40, width - 170, 40); // Moved up
            metricsPanel.setOpaque(false);

            // Total Seek Time
            JLabel seekTimeLabel = new JLabel("Total Seek Time:");
            seekTimeLabel.setBounds(0, 10, 120, 25);
            seekTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            metricsPanel.add(seekTimeLabel);

            totalSeekTimeField = new JTextField(8);
            totalSeekTimeField.setBounds(130, 10, 80, 25);
            totalSeekTimeField.setEditable(false);
            totalSeekTimeField.setBackground(new Color(230, 230, 230));
            totalSeekTimeField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
            totalSeekTimeField.setHorizontalAlignment(JTextField.CENTER);
            metricsPanel.add(totalSeekTimeField);

            // Total Head Movements
            JLabel headMovLabel = new JLabel("Total Head Movements:");
            headMovLabel.setBounds(250, 10, 180, 25);
            headMovLabel.setFont(new Font("Arial", Font.BOLD, 14));
            metricsPanel.add(headMovLabel);

            totalHeadMovementsField = new JTextField(5);
            totalHeadMovementsField.setBounds(430, 10, 80, 25);
            totalHeadMovementsField.setEditable(false);
            totalHeadMovementsField.setBackground(new Color(230, 230, 230));
            totalHeadMovementsField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
            totalHeadMovementsField.setHorizontalAlignment(JTextField.CENTER);
            metricsPanel.add(totalHeadMovementsField);

            contentPanel.add(metricsPanel);

            // Graph Panel
            graphPanel = new GraphPanel();
            graphPanel.setBounds(10, 100, width - 170, 370);
            graphPanel.setBorder(null);
            contentPanel.add(graphPanel);

            add(contentPanel);
        }

        public void setOrderSequence(String sequence) {
            orderSequenceField.setText(sequence);
        }

        public void setTotalSeekTime(int time) {
            totalSeekTimeField.setText(String.valueOf(time));
        }

        public void setTotalHeadMovements(int movements) {
            totalHeadMovementsField.setText(String.valueOf(movements));
        }

        public void setPositions(List<Integer> positions) {
            this.positions = positions;
        }

        public void setCurrentStep(int step) {
            this.currentStep = step;
        }

        public int getMaxSteps() {
            return positions.size() > 0 ? positions.size() - 1 : 0;
        }

        public void updateGraph() {
            graphPanel.repaint();
        }

        /**
         * Graph panel for visualizing the disk head movement
         */
        class GraphPanel extends JPanel {
            private final int TOP_PADDING = 20;
            private final int RULER_POSITION = 15;

            public GraphPanel() {
                setOpaque(true);
                setBackground(Color.WHITE);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                if (positions.isEmpty())
                    return;

                // Find min and max positions to determine the scale
                int minPos = Integer.MAX_VALUE;
                int maxPos = Integer.MIN_VALUE;

                for (Integer pos : positions) {
                    minPos = Math.min(minPos, pos);
                    maxPos = Math.max(maxPos, pos);
                }

                // Ensure we have some padding
                minPos = Math.max(0, minPos - 10);
                maxPos = Math.min(MAX_DISK_SIZE, maxPos + 10);

                // Draw the dynamic scale at the top with increased padding
                g2d.setColor(Color.BLACK);
                g2d.drawLine(0, RULER_POSITION, width, RULER_POSITION);

                // Draw scale with tick marks for better readability
                int numTicks = 10;
                int step = (maxPos - minPos) / numTicks;

                // Ensure step is at least 1
                step = Math.max(1, step);

                // Font for ruler labels
                Font rulerFont = new Font("Arial", Font.PLAIN, 10);
                g2d.setFont(rulerFont);

                for (int i = 0; i <= numTicks; i++) {
                    int pos = minPos + (i * step);
                    if (pos > maxPos)
                        break;

                    int x = scaleX(pos, minPos, maxPos, width);
                    g2d.drawLine(x, RULER_POSITION - 3, x, RULER_POSITION + 3);

                    // Position text above the line with more space
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(String.valueOf(pos));
                    g2d.drawString(String.valueOf(pos), x - textWidth / 2, RULER_POSITION - 5);
                }

                // Draw important tick marks
                drawTickMark(g2d, 0, minPos, maxPos, width, rulerFont);
                drawTickMark(g2d, 50, minPos, maxPos, width, rulerFont);
                drawTickMark(g2d, 100, minPos, maxPos, width, rulerFont);
                drawTickMark(g2d, 150, minPos, maxPos, width, rulerFont);
                drawTickMark(g2d, MAX_DISK_SIZE, minPos, maxPos, width, rulerFont);

                // Scale positions to fit the graph based on the dynamic scale
                int[] xPoints = new int[positions.size()];
                int[] yPoints = new int[positions.size()];

                for (int i = 0; i < positions.size(); i++) {
                    xPoints[i] = scaleX(positions.get(i), minPos, maxPos, width);
                    // Start drawing paths below the ruler with additional padding
                    yPoints[i] = TOP_PADDING + 10 + (i * (height - TOP_PADDING - 20)) / (positions.size() - 1);
                }

                // Draw the path up to current step
                if (currentStep > 0) {
                    g2d.setStroke(new BasicStroke(2));
                    for (int i = 0; i < Math.min(currentStep, positions.size() - 1); i++) {
                        g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);

                        // Draw arrowhead in the middle of the line
                        int midX = (xPoints[i] + xPoints[i + 1]) / 2;
                        int midY = (yPoints[i] + yPoints[i + 1]) / 2;

                        double angle = Math.atan2(yPoints[i + 1] - yPoints[i],
                                xPoints[i + 1] - xPoints[i]);

                        drawArrow(g2d, midX, midY, angle);
                    }
                }

                // Draw circles at each point
                g2d.setColor(Color.BLACK);
                for (int i = 0; i <= Math.min(currentStep, positions.size() - 1); i++) {
                    g2d.fill(new Ellipse2D.Double(xPoints[i] - 4, yPoints[i] - 4, 8, 8));
                }
            }

            private int scaleX(int pos, int minPos, int maxPos, int width) {
                // Scale the position to fit within the panel width with horizontal padding
                return (int) (((double) (pos - minPos) / (maxPos - minPos)) * (width - 20)) + 10;
            }

            private void drawTickMark(Graphics2D g2d, int pos, int minPos, int maxPos, int width, Font font) {
                // Only draw if the position is within the visible range
                if (pos >= minPos && pos <= maxPos) {
                    int x = scaleX(pos, minPos, maxPos, width);
                    g2d.drawLine(x, RULER_POSITION - 3, x, RULER_POSITION + 3);

                    // Position text centered above the tick mark
                    FontMetrics fm = g2d.getFontMetrics(font);
                    int textWidth = fm.stringWidth(String.valueOf(pos));
                    g2d.drawString(String.valueOf(pos), x - textWidth / 2, RULER_POSITION - 5);
                }
            }

            // Draw an arrow at a specific point with a given angle
            private void drawArrow(Graphics2D g2d, int x, int y, double angle) {
                int len = 6; // Arrow length (smaller than in the original)

                // Calculate the arrow head points
                int x1 = (int) (x - len * Math.cos(angle - Math.PI / 6));
                int y1 = (int) (y - len * Math.sin(angle - Math.PI / 6));
                int x2 = (int) (x - len * Math.cos(angle + Math.PI / 6));
                int y2 = (int) (y - len * Math.sin(angle + Math.PI / 6));

                // Calculate the tip of the arrow
                int tipX = (int) (x + len * Math.cos(angle));
                int tipY = (int) (y + len * Math.sin(angle));

                // Save the current color
                Color originalColor = g2d.getColor();

                // Set the arrow color to #FF0000 (bright red)
                g2d.setColor(new Color(255, 0, 0));

                // Draw the arrow head
                Path2D.Double path = new Path2D.Double();
                path.moveTo(tipX, tipY);
                path.lineTo(x1, y1);
                path.lineTo(x2, y2);
                path.closePath();

                g2d.fill(path);

                // Restore the original color
                g2d.setColor(originalColor);
            }
        }
    }

    // Add this class inside the CombinedSimulationScreen class
    class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(81, 82, 84);
            this.trackColor = new Color(136, 137, 139);
            this.thumbDarkShadowColor = Color.BLACK;
            this.thumbHighlightColor = Color.DARK_GRAY;
            this.thumbLightShadowColor = Color.DARK_GRAY;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(thumbColor);
            g2.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);

            g2.dispose();
        }
    }
}