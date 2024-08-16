package prac2;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        
        // ask if the user wants to use the local database or the docker database
        String[] options = {"Local", "Docker"};
        int choice = JOptionPane.showOptionDialog(null, "Select the database to use", "Database Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0) {
            ObjectDBManager.getInstance().local = true;
        } else {
            ObjectDBManager.getInstance().local = false;
        }

        App.launch(App.class, args);
    }
}
