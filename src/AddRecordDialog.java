/*
 * 
 * This is a dialog for adding new Employees and saving records to file
 * 
 * */

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class AddRecordDialog extends JDialog implements ActionListener {
	JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	JButton save, cancel;
	EmployeeDetails parent;
	Validation validation = new Validation();

	public AddRecordDialog(EmployeeDetails parent) {
		setTitle(DisplayValues.add_record);
		setModal(true);
		this.parent = parent;
		this.parent.setEnabled(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane(dialogPane());
		setContentPane(scrollPane);
		
		getRootPane().setDefaultButton(save);
		
		setSize(500, 370);
		setLocation(350, 250);
		setVisible(true);
	}

	// initialize dialog container
	public Container dialogPane() {
		JPanel empDetails, buttonPanel;
		empDetails = new JPanel(new MigLayout());
		buttonPanel = new JPanel();
		JTextField field;

		empDetails.setBorder(BorderFactory.createTitledBorder(DisplayValues.employee_details));

		empDetails.add(new JLabel(DisplayValues.id), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(idField = new JTextField(20), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);
		idField.setEditable(false);
		

		empDetails.add(new JLabel(DisplayValues.pps_number), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(ppsField = new JTextField(20), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.surname), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(surnameField = new JTextField(20), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.first_name), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(firstNameField = new JTextField(20), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.gender), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(genderCombo = new JComboBox<String>(this.parent.gender), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.department), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(departmentCombo = new JComboBox<String>(this.parent.department), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.salary), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(salaryField = new JTextField(20), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.full_time), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(fullTimeCombo = new JComboBox<String>(this.parent.fullTime), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		buttonPanel.add(save = new JButton(DisplayValues.save));
		save.addActionListener(this);
		save.requestFocus();
		buttonPanel.add(cancel = new JButton(DisplayValues.cancel));
		cancel.addActionListener(this);

		empDetails.add(buttonPanel, LayoutOptions.span + ", " + LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);
		// loop through all panel components and add fonts and listeners
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(this.parent.font1);
			if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Colour.white);
			}
			else if(empDetails.getComponent(i) instanceof JTextField){
				field = (JTextField) empDetails.getComponent(i);
				if(field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
				field.setDocument(new JTextFieldLimit(20));
			}
		}
		idField.setText(Integer.toString(this.parent.getNextFreeId()));
		return empDetails;
	}

	// add record to file
	public void addRecord() {
		boolean fullTime = false;
		Employee theEmployee;

		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase(DisplayValues.yes))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(), surnameField.getText().toUpperCase(),
				firstNameField.getText().toUpperCase(), genderCombo.getSelectedItem().toString().charAt(0),
				departmentCombo.getSelectedItem().toString(), Double.parseDouble(salaryField.getText()), fullTime);
		this.parent.currentEmployee = theEmployee;
		this.parent.addRecord(theEmployee);
		this.parent.displayRecords(theEmployee);
	}

	// check for input in text fields
	public boolean checkInput() {
		boolean valid = validation.validateEmpDetails(ppsField, surnameField, firstNameField, genderCombo, departmentCombo, salaryField, fullTimeCombo, -1,
                this.parent.getApplication(), this.parent.getFile());

		return valid;
	}

	// set text field to white colour
	public void setToWhite() {
		ppsField.setBackground(Colour.white);
		surnameField.setBackground(Colour.white);
		firstNameField.setBackground(Colour.white);
		salaryField.setBackground(Colour.white);
		genderCombo.setBackground(Colour.white);
		departmentCombo.setBackground(Colour.white);
		fullTimeCombo.setBackground(Colour.white);
	}


	public void actionPerformed(ActionEvent e) {
		// if chosen option save, save record to file
		if (e.getSource() == save) {
			// if inputs correct, save record
			if (checkInput()) {
				addRecord();
				dispose();
				this.parent.file_change = true;
			}
			// else display message and set text fields to white colour
			else {
				JOptionPane.showMessageDialog(null, DisplayValues.value_format_error);
				setToWhite();
			}
		}
		else if (e.getSource() == cancel)
			dispose();
	}
}