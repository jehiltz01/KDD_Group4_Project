import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.Dimension;

/**
 * GUI For the Action Rules Program
 * @author Jane Hiltz (jhiltz3)
 */
class ActionRulesGUI {

    //Label for the data file the user selects
    public static JLabel dataFileLabel;
    //Label for the name file the user selects
    public static JLabel nameFileLabel;
    //Combo box for the delimiter the user selects
    public static JComboBox delimiterComboBox;
    //Label for the generator 
    public static JLabel generatorLabel;

    //Data file
    public static File dataFile;
    public static boolean dataFileLoaded = false;
    //Name file
    public static File nameFile;
    public static boolean nameFileLoaded = false;
    //Delimiter
    public static String delimiter;

    /**
     * Main method for the ActionRulesGUI program
     * It create the GUI with panels and and a mix of box and flow layouts
     * @param args
     */
    public static void main(String args[]){

        //Main Pannel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));


        // Frame 
        JFrame f = new JFrame("Action Rules Generator");
        f.setSize(600,800);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        

        /// Data File Picker ///
        
        //Data Title Panel
        JPanel dataTitlePanel = new JPanel();
        dataTitlePanel.setMaximumSize(new Dimension(600, 40));
        JLabel dataTitleLabel = new JLabel("Select a .data file to generate action rules from...");
        dataTitlePanel.add(dataTitleLabel);

        //Buttons for file pickers
        JButton dataButton1 = new JButton("Browse...");
        JButton dataButton2 = new JButton("Load");

        //Make the file chooser
        DataFileChooser dataFC = new DataFileChooser();

        //Action listeners for the buttons
        dataButton1.addActionListener(dataFC);
        dataButton2.addActionListener(dataFC);

        //Pannel for the buttons
        JPanel dataPanel = new JPanel();
        dataPanel.setMaximumSize(new Dimension(600, 40));
        dataPanel.add(dataButton1);
        dataPanel.add(dataButton2);

        JPanel dataLabelPanel = new JPanel();
        dataLabelPanel.setMaximumSize(new Dimension(600, 40));
        dataFileLabel = new JLabel("No file selected");
        dataLabelPanel.add(dataFileLabel);

        mainPanel.add(dataTitlePanel);
        mainPanel.add(dataPanel);
        mainPanel.add(dataLabelPanel);


        /// Names File Picker ///

        //Names Title Panel
        JPanel nameTitlePanel = new JPanel();
        nameTitlePanel.setMaximumSize(new Dimension(600, 40));
        JLabel nameTitleLabel = new JLabel("Select a .names file containing the name of your attributes...");
        nameTitlePanel.add(nameTitleLabel);

        JButton nameButton1 = new JButton("Browse...");
        JButton nameButton2 = new JButton("Load");

        //Make the file chooser
        NameFileChooser nameFC = new NameFileChooser();

        //Action listeners for the buttons
        nameButton1.addActionListener(nameFC);
        nameButton2.addActionListener(nameFC);

        //Pannels for the name buttons
        JPanel namePanel = new JPanel();
        namePanel.setMaximumSize(new Dimension(600, 40));
        namePanel.add(nameButton1);
        namePanel.add(nameButton2);
        JPanel nameLablePanel = new JPanel();
        nameLablePanel.setMaximumSize(new Dimension(600, 40));
        nameFileLabel = new JLabel("No file selected");
        nameLablePanel.add(nameFileLabel);
        
        mainPanel.add(nameTitlePanel);
        mainPanel.add(namePanel);
        mainPanel.add(nameLablePanel);


        /// Delimiter Panel ///

        //Delimiter title panel
        JPanel delimiterTitlePanel = new JPanel();
        delimiterTitlePanel.setMaximumSize(new Dimension(600, 40));
        JLabel delimiterTitleLable = new JLabel("Select delimiter for the data file...");
        delimiterTitlePanel.add(delimiterTitleLable);

        //Delimiter dropdown
        JPanel delimiterPanel = new JPanel();
        delimiterPanel.setMaximumSize(new Dimension(600, 40));
        String[] DelimiterOptions = {",", "tab"};
        delimiterComboBox = new JComboBox<>(DelimiterOptions);
        delimiterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt){
                delimiter = delimiterComboBox.getItemAt(delimiterComboBox.getSelectedIndex()).toString(); 
            }
        });
        delimiterPanel.add(delimiterComboBox);

        mainPanel.add(delimiterTitlePanel);
        mainPanel.add(delimiterPanel);

        /// Generate Action Rules Panel ///

        //Generator Title 
        JPanel generatorTitlePanel = new JPanel();
        generatorTitlePanel.setMaximumSize(new Dimension(600, 40));
        JLabel generatorTitleLabel = new JLabel("Generate Action Rules");
        generatorTitlePanel.add(generatorTitleLabel);

        GenerateCommand gComd = new GenerateCommand();        

        //Generator Button
        JPanel generatorPanel = new JPanel();
        generatorPanel.setMaximumSize(new Dimension(600, 40));
        JButton generatorButton = new JButton("Calculate Action Rules");
        generatorButton.addActionListener(gComd);
        generatorPanel.add(generatorButton);

        //Generator Label
        JPanel generatorLablePanel = new JPanel();
        generatorLablePanel.setMaximumSize(new Dimension(600, 40));
        generatorLabel = new JLabel();
        generatorLablePanel.add(generatorLabel);

        mainPanel.add(generatorTitlePanel);
        mainPanel.add(generatorPanel);
        mainPanel.add(generatorLablePanel);

        /// Main Pannel
        f.add(mainPanel);
        f.setVisible(true);
        
    }


    /**
     * Class for a FileChooser object for a data file
     * This allows a user to choose a .data file
     */
    public static class DataFileChooser extends JFrame implements ActionListener {

        //Default constructor
        public DataFileChooser(){
        }

        //Action listeners for the buttons
        public void actionPerformed(ActionEvent evt){
            
            //If user presses browse, then open the 
            String com = evt.getActionCommand();

            if(com.equals("Browse...")){
                File curDirectory = new File(System.getProperty("user.dir"));
                JFileChooser dataFC = new JFileChooser(curDirectory);
                FileNameExtensionFilter dataExtention = new FileNameExtensionFilter("Dataset File", "data"); 
                dataFC.setFileFilter(dataExtention);
                
                int toSelect = dataFC.showOpenDialog(null);
                if(toSelect == JFileChooser.APPROVE_OPTION){
                    dataFile = new File(dataFC.getSelectedFile().getAbsolutePath());
                    dataFileLabel.setText(dataFC.getSelectedFile().getAbsolutePath());
                } else {
                    dataFileLabel.setText("Operation Cancled");
                }

            } else {
                try {
                    if(dataFile.exists()){
                        dataFileLoaded = true;
                        System.out.println("READ DATA FILE");
                    }
                } catch(NullPointerException e){
                    dataFileLoaded = false;
                    System.out.println("No data file to load");
                }
            }
        }

    }

    /**
     * Class for a FileChooser object for a data file
     * This allows a user to choose a .data file
     */
    public static class NameFileChooser extends JFrame implements ActionListener {

        //Default constructor
        public NameFileChooser(){
        }

        //Action listeners for the buttons
        public void actionPerformed(ActionEvent evt){
            
            //If user presses browse, then open the 
            String com = evt.getActionCommand();

            if(com.equals("Browse...")){
                File curDirectory = new File(System.getProperty("user.dir"));
                JFileChooser nameFC = new JFileChooser(curDirectory);
                FileNameExtensionFilter dataExtention = new FileNameExtensionFilter("Attribute Names File", "names"); 
                nameFC.setFileFilter(dataExtention);
                
                int toSelect = nameFC.showOpenDialog(null);
                if(toSelect == JFileChooser.APPROVE_OPTION){
                    nameFile = new File(nameFC.getSelectedFile().getAbsolutePath());
                    nameFileLabel.setText(nameFC.getSelectedFile().getAbsolutePath());
                } else {
                    nameFileLabel.setText("Operation Cancled");
                }

            } else {
                try {
                    if(nameFile.exists()){
                        nameFileLoaded = true;
                        System.out.println("READ NAMES FILE");
                    }
                } catch(NullPointerException e){
                    nameFileLoaded = false;
                    System.out.println("No names file to load");
                }
            }
        }
    }

    /**
     * Class for a GenerateCommand object for the calculate action rules button
     * This calls the code that calculates action rules, provide the user has selected and loaded the data and names file and selected a delimiter
     */
    public static class GenerateCommand extends JFrame implements ActionListener {

        //Default constructor
        public GenerateCommand(){
        }

        //Action listeners for the buttons
        public void actionPerformed(ActionEvent evt){
            
            //If user presses generate button, then generate action rules if the files and delimiter have been loaded/selected
            String com = evt.getActionCommand();

            if(com.equals("Calculate Action Rules")){
                if(dataFileLoaded && nameFileLoaded && delimiter != null){
                    System.out.println("GENERATE RULES");
                    System.out.println("Delimiter is: " + delimiter);
                    generatorLabel.setText("Calculating...");
                } else {
                    generatorLabel.setText(("Must load all files and select a delimiter."));
                }
                
            }

        }

    }



}