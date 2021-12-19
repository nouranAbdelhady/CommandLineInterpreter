import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;



class Parser {
	String commandName;
	String[] args;

	List<String> avaliableCommands = Arrays.asList("echo", "pwd", "cd", "ls", "ls -r", "mkdir", "rmdir", "touch", "cp",
			"cp -r", "rm", "cat", "exit");

	// This method will divide the input into commandName and args
	// where "input" is the string command entered by the user
	public Boolean parse(String input) {

		List<String> list = new ArrayList<String>(Arrays.asList(input.split(" ")));

		// if command has '-r' , add it to the command and remove it from array
		if (list.size() > 1) {
			if (list.get(1).compareTo("-r") == 0) {
				// System.out.println("second word is -r");
				String newCommand = list.get(0) + " " + list.get(1);
				list.remove(1);
				list.set(0, newCommand);
			}
		}

		// make sure command is valid
		boolean validCommand = false;
		for (String singleCommand : avaliableCommands) {
			if (singleCommand.compareTo(list.get(0)) == 0) {
				// System.out.println("Command Found");
				validCommand = true;
				break;
			}
		}

		if (validCommand) {
			commandName = list.get(0);
		} else {
			// System.out.println("Command Not Found");
			commandName = "Error:Invalid Command";
		}

		if (list.size() > 1) {
			args = new String[list.size() - 1];
			list.remove(0); // remove command and pass the rest of the parser
			list.toArray(args);
		}
		return validCommand;

	}

	public String getCommandName() {
		return commandName;
	}

	public String[] getArgs() {
		return args;
	}

}

public class Terminal {
	Parser parser = new Parser();
	private static File currentDirectory;
	private static File homeDirectory;

	Terminal() {
		currentDirectory =new File(System.getProperty("user.dir"));
		homeDirectory = new File("D:\\");
	}

	public Boolean isValidRelativePath(String relativePath) {
		String relativePathConstructed = currentDirectory.getAbsolutePath() + "\\" + relativePath;

		Boolean correctPath = true;
		// check if relative path exists
		File file = new File(relativePathConstructed);
		if (file.isDirectory()) {
			// System.out.println("File is a Directory");
		} else {
			// System.out.println("Error: Invalid path");
			correctPath = false;
		}
		return correctPath;
	}

	public Boolean isValidFullPath(String fullPath) {
		Boolean correctPath = true;

		// check if full path exists
		File file = new File(fullPath);
		if (file.isDirectory()) {
			// System.out.println("File is a Directory");
		} else {
			// System.out.println("Error: Invalid path");
			correctPath = false;
		}
		return correctPath;
	}

	public String pwd() {
		if (parser.args == null) {
			return currentDirectory.toString();
		}
		else {
			return "Error: Invalid number of arguments";
		}
	}

	public void ls() {
		// gets list of files in current directory
		String listOfFiles[] = currentDirectory.list();

		if (parser.args != null) {
			System.out.println("Error: Invalid number of arguments");
		}
		else {
			if (listOfFiles == null) {
				System.out.println("Empty directory");
			} else {
				// print each directory
				for (String singleFile : listOfFiles)
					System.out.println(singleFile);
			}
		}	
	}

	// ls -r
	public void ls(Boolean reverse) {
		List<String> listOfFiles = new ArrayList<String>(Arrays.asList(currentDirectory.list()));

		// reverse list
		Collections.reverse(listOfFiles);

		if (listOfFiles != null) {
			for (String singleFile : listOfFiles)
				System.out.println(singleFile);
		}

	}

	public void cd(String[] args) {
		if (args == null) {
			// System.out.println("No arguments, returning to home directory");
			currentDirectory = homeDirectory;
		} else {
			if (args.length == 1 && (args[0].compareTo("..") == 0)) {
				// System.out.println("Changing current directory to previous directory");
				// System.out.println(currentDirectory.getParent());
				if (currentDirectory.getParent() == null) {
					System.out.println("Error: No parent avaliable");
				} else {
					currentDirectory = currentDirectory.getParentFile();
				}
			} else {
				if (args.length == 1) {
					// System.out.println("Changing directory to given directory: " + args[0]);

					String listOfFiles[] = currentDirectory.list();

					if (listOfFiles == null) {
						System.out.println("Error: No subfiles");
						return;
					} else {
						//check if correct relative path
						if (isValidRelativePath(args[0])) {
							// System.out.println("Correct relative path");
							String relativePath = currentDirectory.getAbsolutePath() + "\\" + args[0];
							currentDirectory = new File(relativePath);
							return;
						} else {
							// System.out.println("Error: Invalid relative path");
							// check if full path correct
							if (isValidFullPath(args[0])) {
								currentDirectory = new File(args[0]);
								return;
							} else {
								// invalid relative and full path
								System.out.println("Error: Invalid path");
							}
						}
					}
				}
				else {
					System.out.println("Error: Invalid number of arguments");
				}
			}
		}
	}

	public void echo(String[] args) {
		if (args == null) {
			System.out.println("Error: No arguments given");
		} else {
			if (args.length == 1) {
				System.out.println(args[0]);
			} else {
				System.out.println("Error: Only 1 argument should be given");
			}
		}
	}

	public void mkdir(String[] args) {

		int numberOfDirectories = args.length;
		String targetedDirectory = currentDirectory.toString();

		// 1st check if relative path
		if (isValidRelativePath(args[args.length - 1])) {
			// System.out.println("File is a Directory");
			String relativePathConstructed = currentDirectory.getAbsolutePath() + "\\" + args[args.length - 1];
			targetedDirectory = relativePathConstructed;
			numberOfDirectories--;
		} else {
			// 2nd check is full path
			if (isValidFullPath(args[args.length - 1])) {
				// System.out.println("File is a Directory");
				targetedDirectory = args[args.length - 1];
				numberOfDirectories--;
			}
			// else: will use current directory
		}

		// System.out.println("Create Directories ");
		// System.out.println("Targeted directory: "+ targetedDirectory);

		File newFile;
		for (int i = 0; i < numberOfDirectories; i++) {
			newFile = new File(targetedDirectory + "\\" + args[i]);
			// Creating the directory

			Boolean createStatus = newFile.mkdirs();
			/*
			 * if (createStatus) { System.out.println(args[i] +
			 * " directory created successfully"); } else { System.out.println("Error: "
			 * +args[i] +" directory already exists"); }
			 */
		}

	}

	//not working
	public void rmdir(String[] args) {
		if (args == null || args.length > 1) {
			System.out.println("Error: Invalid number of arguments given");
		} else {
			if (args[0].compareTo("*") == 0) {
				// System.out.println("Argument: *");

				List<String> listOfDirectories = new ArrayList<String>(Arrays.asList(currentDirectory.list()));
				if (listOfDirectories == null) {
					System.out.println("Empty directory");
				} else {
					for (String singleDirectory : listOfDirectories) {
						Path targetedPath = Paths.get(currentDirectory.toString() + "\\" + singleDirectory);

						// check if empty before deleting
						if (targetedPath.toFile().listFiles().length == 0) {
							File toDelete = new File(targetedPath.toString()); // file to be delete
							if (toDelete.delete()) { // returns Boolean value
								// System.out.println(toDelete.getName() + " deleted"); //getting and printing
								// the file name
							} else {
								System.out.println("Error: No file found");
							}
						} else {
							System.out.println("Error: " + targetedPath.getFileName() + " directory is not empty");
						}
					}

				}

			} else {	//path given to delete
				
				String relativePath = currentDirectory.getAbsolutePath() + "\\" + args[0];
				String targetedDirectory = null;

				// check if relative path exists
				if (isValidRelativePath(relativePath)) {
					targetedDirectory = relativePath;
				}
				else {
					// check if full path exists
					if (isValidFullPath(args[0])) {
						targetedDirectory = args[0];
					}
				}

				if (targetedDirectory == null) {
					System.out.println("Error: Invalid path");
				} else {
					// delete directory
					// System.out.println("Delete");

					Path targetedPath = Paths.get(targetedDirectory);
					// List<String> listOfFiles = new ArrayList<String>( Arrays.asList(
					// targeted.list() ));

					// System.out.println("Number of files:
					// "+targetedPath.toFile().listFiles().length);

					// check if empty before deleting
					if (targetedPath.toFile().listFiles().length == 0) {
						rm(args);
					} else {
						System.out.println("Error: " + args[0] + " directory is not empty");
					}
				}

			}
		}
	}

	public void touch(String[] args) {
		if (args == null || args.length > 1) {
			System.out.println("Invalid number of argumnets");
		}
		// 1 argument
		else {
			String relativePath = currentDirectory.getAbsolutePath() + "\\" + args[0];
			// System.out.println("Full path: "+relativePath);

			Boolean correctPath = true;
			// relative path converted to full path
			try {
				Paths.get(relativePath);
			} catch (InvalidPathException ex) {
				System.out.println("Error: Invalid relative path entered");
				correctPath = false;
			}

			if (correctPath) {
				// System.out.println("Correct relative path");
				// create file in this path
				File file = new File(relativePath);
				Boolean result;

				try {
					result = file.createNewFile(); // creates a new file
					if (result) { // test if successfully created a new file
						// System.out.println("file created "+file.getCanonicalPath()); //returns the
						// path string
					} else {
						System.out.println("Error: File already exist at location: " + file.getCanonicalPath());
					}
				} catch (IOException e) {
					e.printStackTrace(); // prints exception if any
				}

			} else {
				correctPath = true;
				// full path
				try {
					Paths.get(args[0]);
				} catch (InvalidPathException ex) {
					System.out.println("Error: Invalid full path entered");
					correctPath = false;
				}

				if (correctPath) {
					// System.out.println("Correct full path");
					// create file in this path
					File file = new File(relativePath);
					Boolean result;

					try {
						result = file.createNewFile(); // creates a new file
						if (result) { // test if successfully created a new file
							// System.out.println("file created "+file.getCanonicalPath()); //returns the
							// path string
						} else {
							System.out.println("Error: File already exist at location: " + file.getCanonicalPath());
						}
					} catch (IOException e) {
						e.printStackTrace(); // prints exception if any
					}
				}
			}

		}
	}

	public void cp(String[] args) throws IOException {
		if (args == null || args.length > 2) {
			System.out.println("Invalid number of argumnets");
		}
		else {
			File source = new File(currentDirectory.getAbsolutePath() + "\\" + args[0]);
			File destination = new File(currentDirectory.getAbsolutePath() + "\\" + args[1]);

			InputStream is = null;
			OutputStream os = null;

			is = new FileInputStream(source);
			os = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			// System.out.println("Copied Successfuly");
			is.close();
			os.close();
		}
	}

	// cp -r
	public void cp(String[] args, Boolean dummyVariable) throws IOException {
		if (args == null || args.length > 2) {
			System.out.println("Invalid number of argumnets");
		}
		else {
			
			File source = new File(currentDirectory.getAbsolutePath() + "\\" + args[0]);
			File dest = new File(currentDirectory.getAbsolutePath() + "\\" + args[1]);

			for (String f : source.list()) {				File source1 = new File(args[0], f);
				File destination = new File(args[1], f);

				if (source1.isDirectory()) {
					// copy directory
					String[] args3 = new String[2];
					args3[0] = source1.toString();
					args3[1] = destination.toString();
					cp(args3, true);
				} else {
					// copy file
					String[] args2 = new String[2];
					args2[0] = source1.toString();
					args2[1] = destination.toString();
					cp(args2);
				}
			}
			
		}
	}

	public void rm(String[] args) {
		if (args == null || args.length > 1) {
			System.out.println("Error: Invalid number of argumnets");
		}
		// 1 argument
		else {
			String relativePath = currentDirectory.getAbsolutePath() + "\\" + args[0];
			// System.out.println("Full path: "+relativePath);
			try {
				File toDelete = new File(relativePath); // file to be delete
				if (toDelete.delete()) { // returns Boolean value
					// System.out.println(toDelete.getName() + " deleted"); //getting and printing
					// the file name
				} else {
					System.out.println("Error: No file found");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void cat(String[] args) {

		if (args == null || args.length > 2) {
			System.out.println("Error: Invalid number of argumnets");
		} else {

			// 1 argument
			if (args.length == 1) {
				String relativePath = currentDirectory.getAbsolutePath() + "\\" + args[0];
				// System.out.println("Full path: "+relativePath);

				File targetedFile = new File(relativePath);

				if (targetedFile.exists()) {
					// gets file content
					try {
						Scanner fileContent = new Scanner(targetedFile);
						String line = null;
						// loops line by line of the content
						while (fileContent.hasNextLine()) {
							line = fileContent.nextLine();
							System.out.println(line);
						}
					} catch (FileNotFoundException e) {
						// e.printStackTrace();
					}
				} else {
					System.out.println("Error: File doesn't exist");
				}
			}
			// 2 arguments
			else {
				String relativePath1 = currentDirectory.getAbsolutePath() + "\\" + args[0];
				String relativePath2 = currentDirectory.getAbsolutePath() + "\\" + args[1];

				File targetedFile1 = new File(relativePath1);
				File targetedFile2 = new File(relativePath2);

				if (targetedFile1.exists() && targetedFile2.exists()) {
					// gets file content
					try {
						// File 1
						Scanner file1Content = new Scanner(targetedFile1);
						// loops line by line of the content
						String line = null;
						while (file1Content.hasNextLine()) {
							line = file1Content.nextLine();
							System.out.println(line);
						}

						// File 2
						Scanner file2Content = new Scanner(targetedFile2);
						// loops line by line of the content
						while (file2Content.hasNextLine()) {
							line = file2Content.nextLine();
							System.out.println(line);
						}
					} catch (FileNotFoundException e) {
						// e.printStackTrace();
					}
				} else {
					System.out.println("Error: 1 of the files doesn't exist");
				}
			}
		}

	}

	// This method will choose the suitable command method to be called
	public void chooseCommandAction() {

		if (parser.getCommandName().compareTo("Invalid Command") == 0) {
			System.out.println("Invalid command entered, try again");
		} else {
			/*
			 * System.out.println("Command Entered: "+terminal.parser.getCommandName());
			 * System.out.print("Args: "); for (int i=0 ; i<terminal.parser.getArgs().length
			 * ; i++) { System.out.println(terminal.parser.getArgs()[i]); }
			 */
			
			switch (parser.getCommandName()) {
			case "echo":
				echo(parser.getArgs());
				break;
			case "pwd":
				System.out.println(pwd());
				break;
			case "cd":
				cd(parser.getArgs());
				break;
			case "ls":
				ls();
				break;
			case "ls -r":
				ls(true);
				break;
			case "mkdir":
				mkdir(parser.getArgs());
				break;
			case "rmdir":
				rmdir(parser.getArgs());
				break;
			case "touch":
				touch(parser.getArgs());
				break;
			case "cp":
				try {
					cp(parser.getArgs());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "cp -r":
				try {
					cp(parser.getArgs(),true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "rm":
				rm(parser.getArgs());
				break;
			case "cat":
				cat(parser.getArgs());
				break;
			default:
				System.out.println("default");
				break;
			}
		}
	}

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		Terminal terminal = new Terminal();

		while (true) {
			System.out.print(">");
			terminal.parser.args = null;
			String terminalInput = input.nextLine();

			if (terminal.parser.parse(terminalInput))
			{
				if (terminal.parser.getCommandName().compareTo("exit") == 0) {
					break;
				}
				terminal.chooseCommandAction();
			}
			else {
				System.out.println("Error: Invalid command");
			}
		}
	}
}