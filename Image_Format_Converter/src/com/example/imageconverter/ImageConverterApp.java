package com.example.imageconverter;

// Import necessary Swing and ImageIO libraries
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;

public class ImageConverterApp extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7929012939688207644L;
	// UI Components
    private JButton openButton = new JButton("Open File");
    private JButton convertButton = new JButton("Convert");
    private JTextField inputPathField = new JTextField();  // shows selected image path
    private JTextField outputNameField = new JTextField(); // name for output image

    // Radio buttons for output format
    private JRadioButton gifOption = new JRadioButton("GIF");
    private JRadioButton jpgOption = new JRadioButton("JPG");
    private JRadioButton pngOption = new JRadioButton("PNG");
    private JRadioButton bmpOption = new JRadioButton("BMP");
    private ButtonGroup formatGroup = new ButtonGroup(); // groups format options

    private JFileChooser fileChooser = new JFileChooser(); // file chooser dialog

    private JLabel imagePreview = new JLabel(); // image preview label
    private JScrollPane imageScroll = new JScrollPane();   // scroll view for image

    private JCheckBox resizeCheckBox = new JCheckBox("Resize"); // option to resize
    private JTextField widthField = new JTextField("800");      // resize width
    private JTextField heightField = new JTextField("600");     // resize height

    private JCheckBox compressCheckBox = new JCheckBox("Compress JPG"); // compress jpg
    private JSlider qualitySlider = new JSlider(0, 100, 80);            // quality slider

    private BufferedImage currentImage = null; // holds loaded image
    private String selectedFormat = "";        // holds selected output format

    public ImageConverterApp() {
        // Window properties
        setTitle("Image Format Converter - Mussab Zneika");
        setSize(900, 650);
        setLayout(null); // using absolute layout
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 245, 255)); // light background

        initComponents(); // set up UI
        initActions();    // set up actions
    }

    // Setup UI components with sizes, fonts, positions, and styling
    private void initComponents() {
        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel inputLabel = new JLabel("Selected Image:");
        inputLabel.setBounds(20, 20, 120, 25);
        add(inputLabel);

        inputPathField.setBounds(150, 20, 500, 25);
        inputPathField.setEditable(false);
        inputPathField.setFont(font);
        add(inputPathField);

        openButton.setBounds(670, 20, 150, 30);
        openButton.setBackground(new Color(100, 150, 255));
        openButton.setForeground(Color.WHITE);
        openButton.setFont(font);
        add(openButton);

        JLabel outputLabel = new JLabel("Output Name:");
        outputLabel.setBounds(20, 60, 120, 25);
        add(outputLabel);

        outputNameField.setBounds(150, 60, 200, 25);
        outputNameField.setFont(font);
        add(outputNameField);

        // Panel for format selection
        JPanel formatPanel = new JPanel(null);
        formatPanel.setBounds(20, 100, 300, 140);
        formatPanel.setBorder(new TitledBorder("Select Output Format"));
        formatPanel.setBackground(new Color(230, 240, 255));

        gifOption.setBounds(10, 20, 80, 25);
        jpgOption.setBounds(10, 50, 80, 25);
        pngOption.setBounds(10, 80, 80, 25);
        bmpOption.setBounds(10, 110, 80, 25);

        formatGroup.add(gifOption);
        formatGroup.add(jpgOption);
        formatGroup.add(pngOption);
        formatGroup.add(bmpOption);

        formatPanel.add(gifOption);
        formatPanel.add(jpgOption);
        formatPanel.add(pngOption);
        formatPanel.add(bmpOption);
        add(formatPanel);

        // Resize options
        resizeCheckBox.setBounds(350, 100, 100, 25);
        add(resizeCheckBox);

        JLabel widthLabel = new JLabel("Width:");
        widthLabel.setBounds(350, 130, 50, 25);
        add(widthLabel);

        widthField.setBounds(400, 130, 80, 25);
        add(widthField);

        JLabel heightLabel = new JLabel("Height:");
        heightLabel.setBounds(500, 130, 50, 25);
        add(heightLabel);

        heightField.setBounds(550, 130, 80, 25);
        add(heightField);

        // Compression option for JPG
        compressCheckBox.setBounds(350, 170, 150, 25);
        add(compressCheckBox);

        qualitySlider.setBounds(350, 200, 300, 50);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setPaintLabels(true);
        qualitySlider.setMajorTickSpacing(20);
        qualitySlider.setMinorTickSpacing(5);
        qualitySlider.setEnabled(false); // only enabled when compress is checked
        add(qualitySlider);

        // Convert button
        convertButton.setBounds(670, 210, 150, 35);
        convertButton.setBackground(new Color(50, 180, 120));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFont(font);
        add(convertButton);

        // Image preview scroll pane
        imageScroll.setBounds(20, 260, 800, 320);
        imageScroll.setViewportView(imagePreview);
        add(imageScroll);
    }

    // Setup actions for buttons and UI interactions
    private void initActions() {
        // Action for "Open File" button
        openButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedImage img = ImageIO.read(file);
                    if (img == null) {
                        JOptionPane.showMessageDialog(this, "Invalid image file.");
                        return;
                    }
                    currentImage = img;
                    inputPathField.setText(file.getAbsolutePath());
                    // Show scaled image preview
                    imagePreview.setIcon(new ImageIcon(img.getScaledInstance(800, 300, Image.SCALE_SMOOTH)));
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading image.");
                }
            }
        });

        // Update selected format when user picks a format
        gifOption.addActionListener(e -> selectedFormat = "gif");
        jpgOption.addActionListener(e -> selectedFormat = "jpg");
        pngOption.addActionListener(e -> selectedFormat = "png");
        bmpOption.addActionListener(e -> selectedFormat = "bmp");

        // Enable/disable compression slider based on checkboxes
        compressCheckBox.addActionListener(e -> 
            qualitySlider.setEnabled(jpgOption.isSelected() && compressCheckBox.isSelected())
        );
        jpgOption.addActionListener(e -> 
            qualitySlider.setEnabled(compressCheckBox.isSelected())
        );

        // Convert button action
        convertButton.addActionListener(e -> convertImage());
    }

    // Convert and save the image based on user input
    private void convertImage() {
        if (currentImage == null || selectedFormat.isEmpty() || outputNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all fields and select an image.");
            return;
        }

        BufferedImage outputImage = currentImage;

        // Resize logic
        if (resizeCheckBox.isSelected()) {
            try {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                outputImage = resizeImage(currentImage, width, height);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid width or height.");
                return;
            }
        }

        // Define output file path
        String outputPath = "c:/" + outputNameField.getText() + "." + selectedFormat;
        File outputFile = new File(outputPath);

        try {
            if (selectedFormat.equals("jpg") && compressCheckBox.isSelected()) {
                float quality = qualitySlider.getValue() / 100f; // convert 0–100 to 0.0–1.0
                saveCompressedJPEG(outputImage, outputFile, quality);
            } else {
                ImageIO.write(outputImage, selectedFormat, outputFile);
            }
            JOptionPane.showMessageDialog(this, "Image converted and saved to " + outputPath);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving the image.");
        }
    }

    // Resize an image to the specified width and height
    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        int type = original.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : original.getType();
        BufferedImage resized = new BufferedImage(width, height, type);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    // Save a JPEG image with compression
    private void saveCompressedJPEG(BufferedImage image, File file, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) throw new IllegalStateException("No JPEG writer found");

        ImageWriter writer = writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(file);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality); // set quality (0.0 - 1.0)

        writer.write(null, new IIOImage(image, null, null), param);

        ios.close();
        writer.dispose();
    }

    // Entry point to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ImageConverterApp frame = new ImageConverterApp();
            frame.setVisible(true);
        });
    }
}
