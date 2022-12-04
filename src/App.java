import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Exception;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

class EditForm extends JFrame implements ActionListener {
    JButton submitBtn, closeBtn;
    JPanel newPanel;
    JLabel dirLabel, fileNameLabel, oldExtLabel, newExtLabel;
    final JTextField dirTextField, fileNametextField, oldExtTextField, newExtTextield;

    EditForm() {

        dirLabel = new JLabel();
        dirLabel.setText("Path to the file(s)");
        dirTextField = new HintTextField("eg D:\\some\\random\\path");

        fileNameLabel = new JLabel();
        fileNameLabel.setText("Substring(s) present in the file name");
        fileNametextField = new HintTextField("eg string1,string2,string3");

        oldExtLabel = new JLabel();
        oldExtLabel.setText("Current extension");
        oldExtTextField = new HintTextField("eg .jarbkp");

        newExtLabel = new JLabel();
        newExtLabel.setText("New extension");
        newExtTextield = new HintTextField("eg .jar");

        newPanel = new JPanel(new GridLayout(5, 1));

        newPanel.add(dirLabel);
        newPanel.add(dirTextField);

        newPanel.add(fileNameLabel);
        newPanel.add(fileNametextField);

        newPanel.add(oldExtLabel);
        newPanel.add(oldExtTextField);

        newPanel.add(newExtLabel);
        newPanel.add(newExtTextield);

        closeBtn = new JButton("Cancel");
        closeBtn.addActionListener(e -> {
            System.exit(0);
        });

        submitBtn = new JButton("Rename");
        submitBtn.setBounds(100, 210, 90, 25);
        submitBtn.addActionListener(this);

        newPanel.add(submitBtn);
        newPanel.add(closeBtn);

        add(newPanel, BorderLayout.CENTER);

        setTitle("File extension changer for PSIRT related JARs");
    }

    public boolean containsWordsPatternMatch(String inputString, String[] words) {

        StringBuilder regexp = new StringBuilder();
        for (String word : words) {
            regexp.append("(?=.*").append(word).append(")");
        }

        Pattern pattern = Pattern.compile(regexp.toString());

        return pattern.matcher(inputString).find();
    }

    public void actionPerformed(ActionEvent ae) {
        String directory = dirTextField.getText();
        String fileNames = fileNametextField.getText();
        String oldExt = oldExtTextField.getText();
        String newExt = newExtTextield.getText();

        File dir = new File(directory);
        File[] files = dir.listFiles();

        String[] fileNamesArray = fileNames.split(",", 0);

        String logFileName = "renamed_files.txt";
        File logFile = new File(logFileName);
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aa").format(Calendar.getInstance().getTime());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));) {
            int counter = 0;
            writer.write("-------------------" + timeStamp + "-------------------");
            writer.write("\n");
            for (File file : files) {
                if (this.containsWordsPatternMatch(file.getName(), fileNamesArray) && file.getName().endsWith(oldExt)) {
                    String oldName = file.getAbsolutePath();
                    file.renameTo(new File(file.getAbsolutePath().replace(oldExt, newExt)));
                    counter++;
                    String newName = new File(file.getAbsolutePath().replace(oldExt, newExt)).getAbsolutePath();
                    writer.write("Renamed " + oldName + " to " + newName);
                    writer.write("\n");
                }
            }
            writer.write("Total Files modified: " + counter + "\n");
            writer.write("------------------------------------------------------------\n\n");
            JOptionPane.showMessageDialog(null,
                    "Successfully renamed " + counter + " files!\nSaved the output in " + logFile.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error occured :(");
        }
    }
}

class Main {
    public static void main(String arg[]) {
        try {
            EditForm form = new EditForm();
            form.setSize(700, 500);
            form.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
}