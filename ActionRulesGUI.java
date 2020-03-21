import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

class ActionRulesGUI {

    //Lable for what file the user selects
    public static JLabel lab;

    //Data file
    public static File dataFile;

    public static void main(String args[]){
        // Frame 
        JFrame f = new JFrame("File chooser");
        f.setSize(400,400);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Buttons for file pickers
        JButton button1 = new JButton("Browse...");
        JButton button2 = new JButton("Open");

        //Make the file chooser
        FileChooser dataFC = new FileChooser();

        //Action listeners for the buttons
        button1.addActionListener(dataFC);
        button2.addActionListener(dataFC);

        //Pannel for the buttons
        JPanel dataPanel = new JPanel();
        dataPanel.add(button1);
        dataPanel.add(button2);

        lab = new JLabel("No file selected");

        dataPanel.add(lab);
        f.add(dataPanel);
        f.setVisible(true);
    }


    /**
     * Class for a FileChooser object
     * This allows a user to choose a file
     */
    public static class FileChooser extends JFrame implements ActionListener {

        //Default constructor
        public FileChooser(){

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
                    lab.setText(dataFC.getSelectedFile().getAbsolutePath());
                } else {
                    lab.setText("Operation Cancled");
                }

            }
        }

    }

}