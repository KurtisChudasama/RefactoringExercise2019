import javax.swing.*;
import java.io.File;

public class Validation {

    // checks inputs for employee details
    public boolean validateEmpDetails(JTextField ppsField, JTextField surnameField, JTextField firstNameField, JComboBox<String> genderCombo, JComboBox<String> departmentCombo,
                                      JTextField salaryField, JComboBox<String> fullTimeCombo, long currentByteStart, RandomFile application, File file) {
        boolean valid = true;

        if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
            ppsField.setBackground(Colour.red);
            valid = false;
        }
        if (ppsField.isEditable()  && correctPps(ppsField.getText().trim(), currentByteStart, application, file)) {
            ppsField.setBackground(Colour.red);
            valid = false;
        }
        if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
            surnameField.setBackground(Colour.red);
            valid = false;
        }
        if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
            firstNameField.setBackground(Colour.red);
            valid = false;
        }
        if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
            genderCombo.setBackground(Colour.red);
            valid = false;
        }
        if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
            departmentCombo.setBackground(Colour.red);
            valid = false;
        }
        try {

            if (Double.parseDouble(salaryField.getText()) < 0) {
                salaryField.setBackground(Colour.red);
                valid = false;
            }
        }
        catch (NumberFormatException num) {
            if (salaryField.isEditable()) {
                salaryField.setBackground(Colour.red);
                valid = false;
            }
        }
        if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
            fullTimeCombo.setBackground(Colour.red);
            valid = false;
        }

        return valid;
    }

    // check for correct PPS format and look if PPS already in use
    public boolean correctPps(String pps, long currentByte, RandomFile application, File file) {
        boolean ppsExist = false;
        //regex: string must begin with 6 numbers followed by one letter
        String regex = "^[0-9]{6}[a-zA-Z]$";
        // check for correct PPS format based on assignment description
        if (pps.length() == 7) {
            if (pps.matches(regex)) {

                application.openReadFile(file.getAbsolutePath());
                // look in file is PPS already in use
                ppsExist = application.isPpsExist(pps, currentByte);
                application.closeReadFile();
            }
            else
                ppsExist = true;
        }
        else
            ppsExist = true;

        return ppsExist;
    }
}
