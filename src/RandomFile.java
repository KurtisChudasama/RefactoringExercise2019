/*
 * 
 * This class is for accessing, creating and modifying records in a file
 * 
 * */

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RandomFile {
	private RandomAccessFile output;
	private RandomAccessFile input;


	public void createFile(String fileName) {
		RandomAccessFile file = null;

		try
		{
			file = new RandomAccessFile(fileName, "rw");

		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.process_file_error);
			System.exit(1);
		}

		finally {
			try {
				if (file != null)
					file.close();
			}
			catch (IOException ioException) {
				JOptionPane.showMessageDialog(null, DisplayValues.close_file_error);
				System.exit(1);
			}
		}
	}

	// Open file for adding or changing records
	public void openWriteFile(String fileName) {
		try
		{
			output = new RandomAccessFile(fileName, "rw");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.no_exist_file_error);
		}
	}

	// Close file for adding or changing records
	public void closeWriteFile() {
		try
		{
			if (output != null)
				output.close();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.close_file_error);
			System.exit(1);
		}
	}

	// Add records to file
	public long addRecords(Employee employeeToAdd) {
		Employee newEmployee = employeeToAdd;
		long currentRecordStart = 0;

		// object to be written to file
		RandomAccessEmployeeRecord record;

		try
		{
			record = new RandomAccessEmployeeRecord(newEmployee.getEmployeeId(), newEmployee.getPps(),
					newEmployee.getSurname(), newEmployee.getFirstName(), newEmployee.getGender(),
					newEmployee.getDepartment(), newEmployee.getSalary(), newEmployee.getFullTime());

			output.seek(output.length());// Look for proper position
			record.write(output);
			currentRecordStart = output.length();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.write_file_error);
		}

		return currentRecordStart - RandomAccessEmployeeRecord.SIZE;// Return position where object starts in the file
	}

	// Change details for existing object
	public void changeRecords(Employee newDetails, long byteToStart) {
		long currentRecordStart = byteToStart;

		RandomAccessEmployeeRecord record;
		Employee oldDetails = newDetails;
		try
		{
			record = new RandomAccessEmployeeRecord(oldDetails.getEmployeeId(), oldDetails.getPps(),
					oldDetails.getSurname(), oldDetails.getFirstName(), oldDetails.getGender(),
					oldDetails.getDepartment(), oldDetails.getSalary(), oldDetails.getFullTime());

			output.seek(currentRecordStart);// Look for proper position
			record.write(output);
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.write_file_error);
		}
	}

	// Delete existing object
	public void deleteRecords(long byteToStart) {
		long currentRecordStart = byteToStart;

		RandomAccessEmployeeRecord record;

		try
		{
			record = new RandomAccessEmployeeRecord();
			output.seek(currentRecordStart);// Look for proper position
			record.write(output);// Replace existing object with empty object
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.write_file_error);
		}
	}

	// Open file for reading
	public void openReadFile(String fileName) {
		try
		{
			input = new RandomAccessFile(fileName, "r");
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.not_supported_file_error);
		}
	}

	// Close file
	public void closeReadFile() {
		try
		{
			if (input != null)
				input.close();
		}
		catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, DisplayValues.close_file_error);
			System.exit(1);
		}
	}

	// Get position of first record in file
	public long getFirst() {
		long byteToStart = 0;

		try {
			input.length();
		}
		catch (IOException e) {
		}
		
		return byteToStart;
	}

	// Get position of last record in file
	public long getLast() {
		long byteToStart = 0;

		try {
			byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
		}
		catch (IOException e) {
		}

		return byteToStart;
	}

	// Get position of next record in file
	public long getNext(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);// Look for proper position in file
			// if next position is end of file go to start of file, else get next position
			if (byteToStart + RandomAccessEmployeeRecord.SIZE == input.length())
				byteToStart = 0;
			else
				byteToStart = byteToStart + RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException e) {
		}
		catch (IOException e) {
		}
		return byteToStart;
	}

	// Get position of previous record in file
	public long getPrevious(long readFrom) {
		long byteToStart = readFrom;

		try {
			input.seek(byteToStart);// Look for proper position in file
			// if previous position is start of file go to end of file, else get previous position
			if (byteToStart == 0)
				byteToStart = input.length() - RandomAccessEmployeeRecord.SIZE;
			else
				byteToStart = byteToStart - RandomAccessEmployeeRecord.SIZE;
		}
		catch (NumberFormatException e) {
		}
		catch (IOException e) {
		}
		return byteToStart;
	}

	// Get object from file in specified position
	public Employee readRecords(long byteToStart) {
		Employee thisEmp = null;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			input.seek(byteToStart);// Look for proper position in file
			record.read(input);
		}
		catch (IOException e) {
		}
		
		thisEmp = record;

		return thisEmp;
	}

	// Check if PPS Number already in use
	public boolean isPpsExist(String pps, long currentByteStart) {
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();
		boolean ppsExist = false;
		long oldByteStart = currentByteStart;
		long currentByte = 0;

		try {
			// Start from start of file and loop until PPS Number is found or search returned to start position
			while (currentByte != input.length() && !ppsExist) {
				//if PPS Number is in position of current object - skip comparison
				if (currentByte != oldByteStart) {
					input.seek(currentByte);// Look for proper position in file
					record.read(input);
					// If PPS Number already exist in other record display message and stop search
					if (record.getPps().trim().equalsIgnoreCase(pps)) {
						ppsExist = true;
						JOptionPane.showMessageDialog(null, DisplayValues.existing_pps_error);
					}
				}
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
		}

		return ppsExist;
	}

	// Check if any record contains valid ID - greater than 0
	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		long currentByte = 0;
		RandomAccessEmployeeRecord record = new RandomAccessEmployeeRecord();

		try {
			// Start from start of file and loop until valid ID is found or search returned to start position
			while (currentByte != input.length() && !someoneToDisplay) {
				input.seek(currentByte);// Look for proper position in file
				record.read(input);
				// If valid ID exist in stop search
				if (record.getEmployeeId() > 0)
					someoneToDisplay = true;
				currentByte = currentByte + RandomAccessEmployeeRecord.SIZE;
			}
		}
		catch (IOException e) {
		}

		return someoneToDisplay;
	}
}
