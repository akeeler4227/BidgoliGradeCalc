package grade.calc;

//********************************************************************************************
//
// Author: Alex Keeler
// 
// Purpose: This program is a grade calculator. The user is first given the option to enter
// a grade or view the grade history, which is used to calculate overall course grades.
// From there, they are given the option to erase the grade history, allowing the course
// grade to be reset. If the user chooses to enter a grade, their grade is written to a file
// containing their previous submissions. In the file, every entry takes up one line
// (format: recievedScore maxScore). Then, all of the entries including the most recent are
// read and added to a sum, which is used to calculate the overall course grade.
// After receiving their grades, the user will be given the option to resubmit their most
// recent entry (removePreviousLine method). Finally, the user will be asked if they want to
// enter another grade. If they choose yes, the program loops. Otherwise, it terminated.
//
//********************************************************************************************

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.text.DecimalFormat;

public class GradeCalc {
	private static Scanner userInput = new Scanner(System.in);
	private static File scores = new File("AssignmentScores.dat"); // Grade history file 

	private static void viewGradeHistory() throws IOException {
		// Print out grade history with entry numbers
		if (!scores.exists()) scores.createNewFile();
		Scanner fileInput = new Scanner(scores);
		int entryCounter = 0;
		boolean empty = false; // Will become true if the grade history is empty, skipping the prompt to erase the file
		
		if(!fileInput.hasNext()) { // This will activate if the grade history file is empty
			System.out.println("The grade history is empty.");
			System.out.println("Redirecting to grade entry...");
			System.out.println(); // Blank line
			empty = true;
		}
		
		if(!empty) {
			int recievedScore, maxScore;
			int recievedScoreSum = 0;
			int maxScoreSum = 0;
			
			while(fileInput.hasNext()){
				recievedScore = Integer.parseInt(fileInput.next());
				recievedScoreSum += recievedScore;
				maxScore = Integer.parseInt(fileInput.next());
				maxScoreSum += maxScore;
				entryCounter++;
				System.out.println("Entry " + entryCounter + ": " + recievedScore + "/" + maxScore);
			}
			System.out.println(); // Blank line
			fileInput.close();
			
			// Create two digit format for percentage
			DecimalFormat df = new DecimalFormat("0.##");
			// Print percentage grade
			System.out.println("Percentage grade: " + df.format((double)recievedScoreSum / maxScoreSum * 100) + "%");
			
			System.out.print("Would you like to erase the grade history? Y/N ");
			switch(userInput.next()){
				case "Y": case "y":
					System.out.println("Erasing grade history...");
					eraseFile();
					System.out.println(); // Blank line
					break;
				default:
					System.out.println("I'm just going to assume you want to keep the grade history since you can't seem to follow directions");
				case "N": case "n":
					break;
			}
			
			System.out.print("Would you like to enter a new grade? Y/N ");
			switch(userInput.next()) {
			case "Y": case "y":
				System.out.println("Redirecting to grade entry...");
				System.out.println(); // Blank line
				break;
			default:
				System.out.println("I'm just going to assume you don't want to since you can't seem to follow directions");
			case "N": case "n":
				System.out.println("Exiting program...");
				System.exit(1);
			}
		}
	}

	private static void removePreviousLine() throws IOException {
		// Method: Copy all lines to new file except unwanted line, wipe original file, copy new file back to original file
		if (!scores.exists()) scores.createNewFile();
		File tempFile = new File("AssignmentScoresTemp.dat");

		// Create temp file
		tempFile.createNewFile();

		boolean repeat = true; // Used for while loop in which all but the last line is copied to the temp file

		Scanner oldFileIn = new Scanner(scores);
		FileWriter tempFileOut = new FileWriter(tempFile, true);
		// Inputs to the FileWriter class: file, append? (write to next line or overwrite)
		// append is set to true to add on to the end of the file rather than overwriting
		
		// Create temp file
		tempFile.createNewFile();
		
		// Write all but last line to temp file
		// Works by reading line and only writing it to temp file if there is a line after it (leaves out last line)
		while(repeat) {
			int num1 = Integer.parseInt(oldFileIn.next());
			int num2 = Integer.parseInt(oldFileIn.next());
				
			if(oldFileIn.hasNext()) {
				tempFileOut.write(num1 + " " + num2);
				tempFileOut.write("\r\n");
				repeat = true;
			} else repeat = false;
		}
		oldFileIn.close();
		tempFileOut.close();
		
		// Erase original file
		eraseFile();
		
		// Write temp file back to file
		Scanner tempFileIn = new Scanner(tempFile);
		FileWriter newFileOut = new FileWriter(scores, true);
		// Inputs to the FileWriter class: file, append? (write to next line or overwrite)
		// append is set to true to add on to the end of the file rather than overwriting
		
		// Copy temp file back to original file line by line
		while(tempFileIn.hasNext()){
			int tempNum1 = Integer.parseInt(tempFileIn.next());
			int tempNum2 = Integer.parseInt(tempFileIn.next());
			
			newFileOut.write(tempNum1 + " " + tempNum2);
			newFileOut.write("\r\n");
		}
		
		tempFileIn.close();
		newFileOut.close();
		
		// Delete temp file
		tempFile.delete();
	}
	
	private static void eraseFile() throws IOException {
		// Erases file by overwriting it with a blank string
		if (!scores.exists()) scores.createNewFile();
		FileWriter overwrite = new FileWriter(scores, false);
		// Inputs to the FileWriter class: file, append? (write to next line or overwrite)
		// append is set to false in order to overwrite
		overwrite.write("");
		overwrite.close();
	}
	
	public static void main(String[] args) throws IOException {
		if (!scores.exists()) scores.createNewFile();
		// Initialize variables
		int recievedScore, maxScore; // Temporarily used to save user's input before being written to file
		int recievedScoreSum, maxScoreSum; // Used to sum up all entries and calculate the course grade
		boolean repeat = true; // Tells the program whether or not to loop depending on the user's input
		
		// Main menu: Prompts the user to either enter a new grade or erase grade history
		System.out.println("Grade Calculator:");
		System.out.println("To enter a new grade, enter G");
		System.out.print("To view the grade history, enter V ");
		switch(userInput.next()) {
			case "G": case "g":
				repeat = true;
				break;
			case "V": case "v":
				viewGradeHistory();
				repeat = true;
				break;
			default:
				System.out.println("Since you can't seem to follow directions, I'm just going to exit the program.");
				System.out.println(); // Blank line
				repeat = false;
				break;
		}
		
		// Main program start
		while(repeat) {
			// Initialize file input/output
			FileWriter fileOutput = new FileWriter(scores,true);
			// Inputs to the FileWriter class: file, append? (write to next line or overwrite)
			// append is set to true to add on to the end of the file rather than overwriting
			Scanner fileInput = new Scanner(scores);
			// Prompt user to enter score
			System.out.print("Enter recieved score: ");
			recievedScore = userInput.nextInt();
			System.out.print("Enter max score: ");
			maxScore = userInput.nextInt();
			fileOutput.write(recievedScore + " " + maxScore);
			fileOutput.write("\r\n"); // Ends the line
			fileOutput.close();
			
			// Set sums to 0
			recievedScoreSum = 0;
			maxScoreSum = 0;
			
			// Input from file line by line
			while(fileInput.hasNext()) {
				recievedScoreSum += Integer.parseInt(fileInput.next());
				maxScoreSum += Integer.parseInt(fileInput.next());
			}
			fileInput.close();
							
			// Create two digit format for percentage
			DecimalFormat df = new DecimalFormat("0.##");
			
			System.out.println(); // Blank line
			System.out.println("Total score: " + df.format(recievedScoreSum));
			System.out.println("Total max score: " + df.format(maxScoreSum));
			System.out.println(); // Blank line
			System.out.println("Percentage grade: " + df.format((double)recievedScoreSum / maxScoreSum * 100) + "%");
			
			// Prompt the user to re-enter value
			System.out.print("Would you like to re-enter this grade? Y/N ");
			switch(userInput.next()) {
				case "Y": case "y":
					System.out.println("Removing previous line...");
					System.out.println(); // Blank line
					removePreviousLine();
					repeat = true;
					break;
				default:
					System.out.println("I'm just going to assume to said no, since you can't seem to follow directions.");
					System.out.println(); // Blank line
				case "N": case "n":
					// Prompt the user to enter another value (nested switch statement)
					System.out.print("Would you like to enter another grade? Y/N ");
					switch(userInput.next()){
					case "Y": case "y":
						System.out.println(); // Blank line
						repeat = true;
						break;
					default:
						System.out.println("I'm just going to assume to said no, since you can't seem to follow directions.");
						System.out.println(); // Blank line
					case "N": case "n":
						System.out.println("Exiting program...");
						repeat = false;
						break;
					}
					break;
			}
		}
		userInput.close(); // Put outside of the loop to only close when the program terminates
	}
}
