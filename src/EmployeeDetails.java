
/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
//TODO replace all strings with values from a String value class
public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
	// decimal format for inactive currency text field
	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	// decimal format for active currency text field
	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	// hold object start position in file
	private long currentByteStart = 0;
	private RandomFile application = new RandomFile();

	public RandomFile getApplication() {
		return application;
	}

	// display files in File Chooser only with extension .dat
	private FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");

	private File file;

	public File getFile() {
		return file;
	}
	//TODO booleans below need name changes
	// holds true or false if any changes are made for text fields
	private boolean change = false;
	// holds true or false if any changes are made for file content
	boolean changesMade = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
			searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
			saveChange, cancelChange;
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	private static EmployeeDetails frame = new EmployeeDetails();
	// font for labels, text fields and combo boxes
	//TODO make font class
	Font font1 = new Font("SansSerif", Font.BOLD, 16);

	String generatedFileName;

	Employee currentEmployee;
	JTextField searchByIdField, searchBySurnameField;

	String[] gender = { DisplayValues.empty, DisplayValues.male, DisplayValues.female };

	String[] department = { DisplayValues.empty, DisplayValues.administration, DisplayValues.production, DisplayValues.transport, DisplayValues.management };

	String[] fullTime = { DisplayValues.empty, DisplayValues.yes, DisplayValues.no };

	Validation validation = new Validation();

	private String type;

	private JMenuBar menuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu, recordMenu, navigateMenu, closeMenu;

		fileMenu = new JMenu(DisplayValues.file);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		recordMenu = new JMenu(DisplayValues.records);
		recordMenu.setMnemonic(KeyEvent.VK_R);
		navigateMenu = new JMenu(DisplayValues.navigate);
		navigateMenu.setMnemonic(KeyEvent.VK_N);
		closeMenu = new JMenu(DisplayValues.exit);
		closeMenu.setMnemonic(KeyEvent.VK_E);

		menuBar.add(fileMenu);
		menuBar.add(recordMenu);
		menuBar.add(navigateMenu);
		menuBar.add(closeMenu);

		fileMenu.add(open = new JMenuItem(DisplayValues.open)).addActionListener(this);
		open.setMnemonic(KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(save = new JMenuItem(DisplayValues.save)).addActionListener(this);
		save.setMnemonic(KeyEvent.VK_S);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fileMenu.add(saveAs = new JMenuItem(DisplayValues.save_as)).addActionListener(this);
		saveAs.setMnemonic(KeyEvent.VK_F2);
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK));

		recordMenu.add(create = new JMenuItem(DisplayValues.new_record)).addActionListener(this);
		create.setMnemonic(KeyEvent.VK_N);
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		recordMenu.add(modify = new JMenuItem(DisplayValues.modify_record)).addActionListener(this);
		modify.setMnemonic(KeyEvent.VK_E);
		modify.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		recordMenu.add(delete = new JMenuItem(DisplayValues.delete_record)).addActionListener(this);

		navigateMenu.add(firstItem = new JMenuItem(DisplayValues.first));
		firstItem.addActionListener(this);
		navigateMenu.add(prevItem = new JMenuItem(DisplayValues.previous));
		prevItem.addActionListener(this);
		navigateMenu.add(nextItem = new JMenuItem(DisplayValues.next));
		nextItem.addActionListener(this);
		navigateMenu.add(lastItem = new JMenuItem(DisplayValues.last));
		lastItem.addActionListener(this);
		navigateMenu.addSeparator();
		navigateMenu.add(searchById = new JMenuItem(DisplayValues.id_search)).addActionListener(this);
		navigateMenu.add(searchBySurname = new JMenuItem(DisplayValues.surname_search)).addActionListener(this);
		navigateMenu.add(listAll = new JMenuItem(DisplayValues.all_records)).addActionListener(this);

		closeMenu.add(closeApp = new JMenuItem(DisplayValues.close)).addActionListener(this);
		closeApp.setMnemonic(KeyEvent.VK_F4);
		closeApp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.CTRL_MASK));

		return menuBar;
	}


	private JPanel searchPanel() {
		JPanel searchPanel = new JPanel(new MigLayout());

		searchPanel.setBorder(BorderFactory.createTitledBorder(DisplayValues.search));
		searchPanel.add(new JLabel(DisplayValues.id_search_colon), LayoutOptions.grow + ", " + LayoutOptions.push);
		searchPanel.add(searchByIdField = new JTextField(20), "width 200:200:200, " + LayoutOptions.grow + ", " + LayoutOptions.push);
		searchByIdField.addActionListener(this);
		searchByIdField.setDocument(new JTextFieldLimit(20));
		//TODO add image class for image strings
		searchPanel.add(searchId = new JButton(new ImageIcon(
				new ImageIcon("imgres.png").getImage().getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, " + LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);
		searchId.addActionListener(this);
		searchId.setToolTipText(DisplayValues.employee_by_id_search);

		searchPanel.add(new JLabel(DisplayValues.surname_search_colon), LayoutOptions.grow + ", " + LayoutOptions.push);
		searchPanel.add(searchBySurnameField = new JTextField(20), "width 200:200:200, " + LayoutOptions.grow + ", " + LayoutOptions.push);
		searchBySurnameField.addActionListener(this);
		searchBySurnameField.setDocument(new JTextFieldLimit(20));
		searchPanel.add(
				searchSurname = new JButton(new ImageIcon(new ImageIcon("imgres.png").getImage()
						.getScaledInstance(35, 20, java.awt.Image.SCALE_SMOOTH))),
				"width 35:35:35, height 20:20:20, " + LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);
		searchSurname.addActionListener(this);
		searchSurname.setToolTipText(DisplayValues.employee_by_surname_search);

		return searchPanel;
	}


	private JPanel navigPanel() {
		JPanel navigPanel = new JPanel();

		navigPanel.setBorder(BorderFactory.createTitledBorder(DisplayValues.navigate));
		navigPanel.add(first = new JButton(new ImageIcon(
				new ImageIcon("first.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		first.setPreferredSize(new Dimension(17, 17));
		first.addActionListener(this);
		first.setToolTipText(DisplayValues.display_first);

		navigPanel.add(previous = new JButton(new ImageIcon(new ImageIcon("previous.png").getImage()
				.getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		previous.setPreferredSize(new Dimension(17, 17));
		previous.addActionListener(this);
		previous.setToolTipText(DisplayValues.display_next);

		navigPanel.add(next = new JButton(new ImageIcon(
				new ImageIcon("next.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		next.setPreferredSize(new Dimension(17, 17));
		next.addActionListener(this);
		next.setToolTipText(DisplayValues.display_previous);

		navigPanel.add(last = new JButton(new ImageIcon(
				new ImageIcon("last.png").getImage().getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH))));
		last.setPreferredSize(new Dimension(17, 17));
		last.addActionListener(this);
		last.setToolTipText(DisplayValues.display_last);

		return navigPanel;
	}

	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton(DisplayValues.add_record), LayoutOptions.grow + ", " + LayoutOptions.push);
		add.addActionListener(this);
		add.setToolTipText(DisplayValues.add_new_employee);
		buttonPanel.add(edit = new JButton(DisplayValues.edit_record), LayoutOptions.grow + ", " + LayoutOptions.push);
		edit.addActionListener(this);
		edit.setToolTipText(DisplayValues.edit_current_employee);
		buttonPanel.add(deleteButton = new JButton(DisplayValues.delete_record), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText(DisplayValues.delete_current_employee);
		buttonPanel.add(displayAll = new JButton(DisplayValues.all_records), LayoutOptions.grow + ", " + LayoutOptions.push);
		displayAll.addActionListener(this);
		displayAll.setToolTipText(DisplayValues.all_registered_employees);

		return buttonPanel;
	}


	private JPanel detailsPanel() {
		JPanel empDetails = new JPanel(new MigLayout());
		JPanel buttonPanel = new JPanel();
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
		empDetails.add(genderCombo = new JComboBox<String>(gender), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.department), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(departmentCombo = new JComboBox<String>(department), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.salary), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(salaryField = new JTextField(20), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		empDetails.add(new JLabel(DisplayValues.full_time), LayoutOptions.grow + ", " + LayoutOptions.push);
		empDetails.add(fullTimeCombo = new JComboBox<String>(fullTime), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		buttonPanel.add(saveChange = new JButton(DisplayValues.save));
		saveChange.addActionListener(this);
		saveChange.setVisible(false);
		saveChange.setToolTipText(DisplayValues.save_changes);
		buttonPanel.add(cancelChange = new JButton(DisplayValues.cancel));
		cancelChange.addActionListener(this);
		cancelChange.setVisible(false);
		cancelChange.setToolTipText(DisplayValues.cancel_edit);

		empDetails.add(buttonPanel, LayoutOptions.span + ", " + LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.wrap);

		// loop through panel components and add listeners and format
		for (int i = 0; i < empDetails.getComponentCount(); i++) {
			empDetails.getComponent(i).setFont(font1);
			if (empDetails.getComponent(i) instanceof JTextField) {
				field = (JTextField) empDetails.getComponent(i);
				field.setEditable(false);
				if (field == ppsField)
					field.setDocument(new JTextFieldLimit(9));
				else
					field.setDocument(new JTextFieldLimit(20));
				field.getDocument().addDocumentListener(this);
			}
			else if (empDetails.getComponent(i) instanceof JComboBox) {
				empDetails.getComponent(i).setBackground(Colour.white);
				empDetails.getComponent(i).setEnabled(false);
				((JComboBox<String>) empDetails.getComponent(i)).addItemListener(this);
				((JComboBox<String>) empDetails.getComponent(i)).setRenderer(new DefaultListCellRenderer() {
					// set foreground to combo boxes
					public void paint(Graphics g) {
						setForeground(new Color(65, 65, 65));
						super.paint(g);
					}
				});
			}
		}
		return empDetails;
	}

	// display current Employee details
	public void displayRecords(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");
		// if Employee is null or ID is 0 do nothing else display Employee details
		if (thisEmployee == null) {
		} else if (thisEmployee.getEmployeeId() == 0) {
		} else {
			// find corresponding gender combo box value to current employee
			while (!found && countGender < gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
					found = true;
				else
					countGender++;
			}
			found = false;
			// find corresponding department combo box value to current employee
			while (!found && countDep < department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
					found = true;
				else
					countDep++;
			}
			idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			ppsField.setText(thisEmployee.getPps().trim());
			surnameField.setText(thisEmployee.getSurname().trim());
			firstNameField.setText(thisEmployee.getFirstName());
			genderCombo.setSelectedIndex(countGender);
			departmentCombo.setSelectedIndex(countDep);
			salaryField.setText(format.format(thisEmployee.getSalary()));
			// set corresponding full time combo box value to current employee
			if (thisEmployee.getFullTime() == true)
				fullTimeCombo.setSelectedIndex(1);
			else
				fullTimeCombo.setSelectedIndex(2);
		}
		change = false;
	}


	private void displayEmployeeSummaryDialog() {

		if (isSomeoneToDisplay())
			new EmployeeSummaryDialog(getAllEmloyees());
	}

	private void displaySearchByDialog(String type) {
		if (isSomeoneToDisplay())
			new SearchByDialog(EmployeeDetails.this, type);
	}

	/*private void displaySearchByIdDialog() {
		if (isSomeoneToDisplay())
			new SearchByIdDialog(EmployeeDetails.this);
	}


	private void displaySearchBySurnameDialog() {
		if (isSomeoneToDisplay())
			new SearchBySurnameDialog(EmployeeDetails.this);
	}*/

	// find byte start in file for first active record
	private void firstRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for first record
			currentByteStart = application.getFirst();

			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();
			// if first record is inactive look for next record
			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();
		}
	}

	// find byte start in file for previous active record
	private void previousRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for previous record
			currentByteStart = application.getPrevious(currentByteStart);

			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {

				currentByteStart = application.getPrevious(currentByteStart);

				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	// find byte start in file for next active record
	private void nextRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for next record
			currentByteStart = application.getNext(currentByteStart);

			currentEmployee = application.readRecords(currentByteStart);

			while (currentEmployee.getEmployeeId() == 0) {

				currentByteStart = application.getNext(currentByteStart);

				currentEmployee = application.readRecords(currentByteStart);
			}
			application.closeReadFile();
		}
	}

	// find byte start in file for last active record
	private void lastRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {

			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for last record
			currentByteStart = application.getLast();

			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();

			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();
		}
	}

	// search Employee by ID
	public void searchEmployeeById() {
		boolean found = false;

		try {
				// if any active Employee record search for ID else do nothing
			if (isSomeoneToDisplay()) {
				firstRecord();
				int firstId = currentEmployee.getEmployeeId();
				// if ID to search is already displayed do nothing else loop through records
				if (searchByIdField.getText().trim().equals(idField.getText().trim()))
					found = true;
				else if (searchByIdField.getText().trim().equals(Integer.toString(currentEmployee.getEmployeeId()))) {
					found = true;
					displayRecords(currentEmployee);
				}
				else {
					nextRecord();
					// loop until Employee found or until all Employees have been checked
					while (firstId != currentEmployee.getEmployeeId()) {
						// if found, break from loop and display Employee details

						if (Integer.parseInt(searchByIdField.getText().trim()) == currentEmployee.getEmployeeId()) {
							found = true;
							displayRecords(currentEmployee);
							break;
						} else// else look for next record
							nextRecord();
					}
				}

				if (!found)
					JOptionPane.showMessageDialog(null, DisplayValues.employee_not_found_error);
			}
		}
		catch (NumberFormatException e) {
			searchByIdField.setBackground(Colour.red);
			JOptionPane.showMessageDialog(null, DisplayValues.id_format_error);
		}
		searchByIdField.setBackground(Colour.white);
		searchByIdField.setText("");
	}


	public void searchEmployeeBySurname() {
		boolean found = false;
		// if any active Employee record search for ID else do nothing
		if (isSomeoneToDisplay()) {
			firstRecord();
			String firstSurname = currentEmployee.getSurname().trim();
			// if ID to search is already displayed do nothing else loop through records
			if (searchBySurnameField.getText().trim().equalsIgnoreCase(surnameField.getText().trim()))
				found = true;
			else if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
				found = true;
				displayRecords(currentEmployee);
			}
			else {
				nextRecord();
				// loop until Employee found or until all Employees have been checked
				while (!firstSurname.trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
					// if found break from loop and display Employee details

					if (searchBySurnameField.getText().trim().equalsIgnoreCase(currentEmployee.getSurname().trim())) {
						found = true;
						displayRecords(currentEmployee);
						break;
					}
					else // else look for next record
						nextRecord();
				}
			}

			if (!found)
				JOptionPane.showMessageDialog(null, DisplayValues.employee_not_found_error);
		}
		searchBySurnameField.setText("");
	}

	// get next free ID from Employees in the file
	public int getNextFreeId() {
		int nextFreeId = 0;
		// if file is empty or all records are empty start with ID 1 else look for last active record
		if (file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();

			nextFreeId = currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}

	// get values from text fields and create Employee object
	private Employee getChangedDetails() {
		boolean fullTime = false;
		Employee theEmployee;
		if (((String) fullTimeCombo.getSelectedItem()).equalsIgnoreCase(DisplayValues.yes))
			fullTime = true;

		theEmployee = new Employee(Integer.parseInt(idField.getText()), ppsField.getText().toUpperCase(),
				surnameField.getText().toUpperCase(), firstNameField.getText().toUpperCase(),
				genderCombo.getSelectedItem().toString().charAt(0), departmentCombo.getSelectedItem().toString(),
				Double.parseDouble(salaryField.getText()), fullTime);

		return theEmployee;
	}

	// add Employee object to file
	public void addRecord(Employee newEmployee) {

		application.openWriteFile(file.getAbsolutePath());

		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();
	}

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
		if (isSomeoneToDisplay()) {// if any active record in file display message and delete record
			int returnVal = JOptionPane.showOptionDialog(frame, DisplayValues.delete_record_question, DisplayValues.delete,
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if answer yes delete (make inactive - empty) record
			if (returnVal == JOptionPane.YES_OPTION) {

				application.openWriteFile(file.getAbsolutePath());

				application.deleteRecords(currentByteStart);
				application.closeWriteFile();

				if (isSomeoneToDisplay()) {
					nextRecord();
					displayRecords(currentEmployee);
				}
			}
		}
	}

	// create vector of vectors with all Employee details
	private Vector<Object> getAllEmloyees() {

		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();
		firstId = currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();
		} while (firstId != currentEmployee.getEmployeeId());
		currentByteStart = byteStart;

		return allEmployee;
	}

	// activate field for editing
	private void editDetails() {

		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);
		}
	}

	// ignore changes and set text field unenabled
	private void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}

	// check if any of records in file is active - ID is not 0
	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;

		application.openReadFile(file.getAbsolutePath());

		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();

		if (!someoneToDisplay) {
			currentEmployee = null;
			idField.setText("");
			ppsField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			salaryField.setText("");
			genderCombo.setSelectedIndex(0);
			departmentCombo.setSelectedIndex(0);
			fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, DisplayValues.no_employees_registered);
		}
		return someoneToDisplay;
	}

	// check if file name has extension .dat
	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}

	// check if any changes text field where made
	private boolean checkForChanges() {
		boolean anyChanges = false;

		if (change) {
			saveChanges();
			anyChanges = true;
		}
			// if no changes made, set text fields as unenabled and display current Employee
		else {
			setEnabled(false);
			displayRecords(currentEmployee);
		}

		return anyChanges;
	}

	// check for input in text fields
	private boolean checkInput() {

		boolean valid = validation.validateEmpDetails(ppsField, surnameField, firstNameField, genderCombo, departmentCombo, salaryField, fullTimeCombo,
				currentByteStart, application, file);

		if (!valid)
			JOptionPane.showMessageDialog(null, DisplayValues.value_format_error);

		if (ppsField.isEditable())
			setToWhite();

		return valid;
	}
//TODO doing same as above
	//TODO textfield.background change?

	private void setToWhite() {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}

	// enable text fields for editing
	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(search);
		searchBySurnameField.setEnabled(search);
		searchId.setEnabled(search);
		searchSurname.setEnabled(search);
	}


	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(DisplayValues.open);
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(datfilter);
		File newFile;
		// if old file is not empty or changes has been made, offer user to save old file
		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, DisplayValues.save_changes_question, DisplayValues.save,
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();
			}
		}

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name, delete this file
			if (file.getName().equals(generatedFileName))
				file.delete();
			file = newFile;

			application.openReadFile(file.getAbsolutePath());
			firstRecord();
			displayRecords(currentEmployee);
			application.closeReadFile();
		}
	}


	private void saveFile() {
		// if file name is generated file name, save file as 'save as' else save changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();
		else {
			// if changes has been made to text field offer user to save these changes
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, DisplayValues.save_changes_question, DisplayValues.save,
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {

					if (!idField.getText().equals("")) {

						application.openWriteFile(file.getAbsolutePath());

						currentEmployee = getChangedDetails();

						application.changeRecords(currentEmployee, currentByteStart);
						application.closeWriteFile();
					}
				}
			}

			displayRecords(currentEmployee);
			setEnabled(false);
		}
	}

	// save changes to current Employee
	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, DisplayValues.save_changes_to_employee_question, DisplayValues.save,
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {

			application.openWriteFile(file.getAbsolutePath());

			currentEmployee = getChangedDetails();

			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();
			changesMade = false;
		}
		displayRecords(currentEmployee);
		setEnabled(false);
	}

	// save file as 'save as'
	private void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle(DisplayValues.save_as);
		// display files only with .dat extension
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText(DisplayValues.save);
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(EmployeeDetails.this);
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();

			if (!checkFileName(newFile)) {
				// add .dat extension if it was not there
				newFile = new File(newFile.getAbsolutePath() + ".dat");

				application.createFile(newFile.getAbsolutePath());
			}
			else

				application.createFile(newFile.getAbsolutePath());

			try {
				Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				// if old file name was generated file name, delete it
				if (file.getName().equals(generatedFileName))
					file.delete();
				file = newFile;
			}
			catch (IOException e) {
			}
		}
		changesMade = false;
	}

	// allow to save changes to file when exiting the application
	private void exitApp() {

		if (file.length() != 0) {
			if (changesMade) {
				int returnVal = JOptionPane.showOptionDialog(frame, DisplayValues.save_changes_question, DisplayValues.save,
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// if user chooses to save file, save file
				if (returnVal == JOptionPane.YES_OPTION) {
					saveFile();
					// delete generated file if user saved details to other file
					if (file.getName().equals(generatedFileName))
						file.delete();
					System.exit(0);
				}

				else if (returnVal == JOptionPane.NO_OPTION) {

					if (file.getName().equals(generatedFileName))
						file.delete();
					System.exit(0);
				}
			}
			else {

				if (file.getName().equals(generatedFileName))
					file.delete();
				System.exit(0);
			}

		} else {

			if (file.getName().equals(generatedFileName))
				file.delete();
			System.exit(0);
		}
	}

	// generate 20 character long file name
	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();

		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}

	// create file with generated file name when application is opened
	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";

		file = new File(generatedFileName);

		application.createFile(file.getName());
	}

	// action listener for buttons, text field and menu items
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == closeApp) {
			if (checkInput() && !checkForChanges())
				exitApp();
		} else if (e.getSource() == open) {
			if (checkInput() && !checkForChanges())
				openFile();
		} else if (e.getSource() == save) {
			if (checkInput() && !checkForChanges())
				saveFile();
			change = false;
		} else if (e.getSource() == saveAs) {
			if (checkInput() && !checkForChanges())
				saveFileAs();
			change = false;
		} else if (e.getSource() == searchById) {
			if (checkInput() && !checkForChanges()) {
				type = "ID";
				displaySearchByDialog(type);
			}
		} else if (e.getSource() == searchBySurname) {
			if (checkInput() && !checkForChanges()) {
				type = "Surname";
				displaySearchByDialog(type);
			}
		} else if (e.getSource() == searchId || e.getSource() == searchByIdField)
			searchEmployeeById();
		else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField)
			searchEmployeeBySurname();
		else if (e.getSource() == saveChange) {
			if (checkInput() && !checkForChanges())
				;
		} else if (e.getSource() == cancelChange)
			cancelChange();
		else if (e.getSource() == firstItem || e.getSource() == first) {
			if (checkInput() && !checkForChanges()) {
				firstRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == prevItem || e.getSource() == previous) {
			if (checkInput() && !checkForChanges()) {
				previousRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == nextItem || e.getSource() == next) {
			if (checkInput() && !checkForChanges()) {
				nextRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == lastItem || e.getSource() == last) {
			if (checkInput() && !checkForChanges()) {
				lastRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == listAll || e.getSource() == displayAll) {
			if (checkInput() && !checkForChanges())
				if (isSomeoneToDisplay())
					displayEmployeeSummaryDialog();
		} else if (e.getSource() == create || e.getSource() == add) {
			if (checkInput() && !checkForChanges())
				new AddRecordDialog(EmployeeDetails.this);
		} else if (e.getSource() == modify || e.getSource() == edit) {
			if (checkInput() && !checkForChanges())
				editDetails();
		} else if (e.getSource() == delete || e.getSource() == deleteButton) {
			if (checkInput() && !checkForChanges())
				deleteRecord();
		} else if (e.getSource() == searchBySurname) {
			if (checkInput() && !checkForChanges())
				type = "Surname";
				new SearchByDialog(EmployeeDetails.this, type);
		}
	}

	// content pane for main dialog
	private void createContentPane() {
		setTitle(DisplayValues.employee_details);
		createRandomFile();
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());

		dialog.add(searchPanel(), "width 400:400:400, " + LayoutOptions.grow + ", " + LayoutOptions.push);

		dialog.add(navigPanel(), "width 150:150:150, " + LayoutOptions.wrap);

		dialog.add(buttonPanel(), LayoutOptions.grow + ", " + LayoutOptions.push + ", " + LayoutOptions.span + ", " + LayoutOptions.wrap);

		dialog.add(detailsPanel(), "gap top 30, gap left 150, " + LayoutOptions.center);

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}

	// create and show main dialog
	private static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}


	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {

		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}
