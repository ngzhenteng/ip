package main.java.duke;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The class responsible for taking in user commands and displaying Duke's responses
 */
public class Ui {

    private TaskList taskList = null;
    private Storage storage = null;
    private Parser parser = new Parser();

    /**
     * Creates an instance of the Ui class
     * @param taskList taskList object used to store tasks
     * @param storage storage object used to read and write for the tasklist.txt file
     */
    public Ui(TaskList taskList, Storage storage) {
        this.taskList = taskList;
        this.storage = storage;
    }


    /**
     * Prints a horizontal line of fixed length.
     */
    public void horiLine(int length) {
        for (int i = 0; i < length; i++) {
            System.out.print("-");
            if (i == length - 1) System.out.println("");
        }
    }

    /**
     * Adds the passed task to the list of tasks and returns the response from Duke as a string
     * @param toAdd The Task object to be added to the list of tasks
     * @return The string response from duke for the input command.
     */
    public String handleAddTask(Task toAdd) throws ArrayIndexOutOfBoundsException {
        storage.writeToFile(toAdd.toString());
        taskList.addTask(toAdd);
        return "  Got it. I've added this task:\n"
                + "    " + toAdd.toString() + "\n"
                + "  Now you have " + (taskList.size()+1) + " tasks in the list.";
    }

    /**
     * Marks the task at index taskIndex of the task list as done, thereafter updates the contents
     * of the local text file storage and returns the string response from Duke.
     * @param taskIndex index of the task in the task list, 0-indexed
     * @return the string response from Duke for the given 'done' command
     * @throws IndexOutOfBoundsException
     * @throws NumberFormatException
     */
    public String markTaskDone(int taskIndex) throws IndexOutOfBoundsException, NumberFormatException {
        Task toMark = taskList.getTask(taskIndex);
        toMark.markDone();
        storage.changeFileContents(taskList);
        return "  Nice! I've marked this task as done:\n"
                + "    " + toMark.toString();
    }

    /**
     * Deletes the task at index taskIndex of the task list, updates the contents
     * of the local text file storage and returns the string response from Duke.
     * @param taskIndex index of the task in the task list, 0-indexed
     * @return the string response from Duke for the given 'delete' command
     * @throws IndexOutOfBoundsException
     * @throws NumberFormatException
     */
    public String deleteTask(int taskIndex) throws IndexOutOfBoundsException, NumberFormatException {
        Task toDisplay = taskList.getTask(taskIndex);
        taskList.removeTask(taskIndex);
        storage.changeFileContents(taskList);
        return "  Noted. I've removed this task:\n"
                + "    " + toDisplay.toString() + "\n"
                + "  Now you have " + taskList.size() + " tasks in the list.";
    }

    /**
     * Takes in the user command and passes it to the parser object, to receive the correct Task
     * to be handled appropriately. Thereafter Duke's response is returned.
     * @param nextInput user input commands that involve the creation of task objects.
     * @return the string to be displayed as the response on CLI or GUI
     */
    public String getResultFromParser(String nextInput) {
        String toReturn;
        // pass user input to parser
        try {
            Task toAdd = parser.handleInput(nextInput);
            if (toAdd == null) {
                DukeException invInputException = new DukeException();
                toReturn = invInputException.getMessage();
            } else {
                toReturn = handleAddTask(toAdd);
            }
        } catch (EmptyDescriptionException ede) {
            toReturn = ede.getMessage();
        } catch (ArrayIndexOutOfBoundsException e) {
            DeadlineFormatException dFormatException = new DeadlineFormatException();
            toReturn = dFormatException.getMessage();
        } catch (DateTimeParseException dtpe) {
            DeadlineFormatException dFormatException = new DeadlineFormatException("Sorry, please key in a " +
                    "valid date and time " +
                    "format");
            toReturn = dFormatException.getMessage();
        }
        return toReturn;
    }

    /**
     * Handles the user input command and returns the respective string responses to the given command. Commands that
     * involves the creation of tasks will be passed to the getResultFromParser method.
     * @param nextInput User input command
     * @return the string to be displayed as the response on CLI or GUI
     */
    public String getDukeResponse(String nextInput) {
        String[] commandComponents = nextInput.split(" ", 2);
        String taskType = commandComponents[0];
        String toReturn;
        if (nextInput.equals("hello")) {
            toReturn = "  Hey! welcome to your nightmare!";
        } else if (nextInput.equals("bye")) {
            toReturn = "  Bye. Hope to see you again soon!";
        } else if (nextInput.equals("list")) {
            toReturn = taskList.toString();
        } else if (commandComponents[0].equals("done")) {
            try {
                int taskIndex = Integer.parseInt(commandComponents[1]) - 1;
                toReturn = markTaskDone(taskIndex);
            } catch (IndexOutOfBoundsException e) {
                DoneException doneException = new DoneException("The task number does not exist!");
                toReturn = doneException.getMessage();
            } catch (NumberFormatException nfe) {
                DoneException doneException =
                        new DoneException("Please provide a task number to mark as done!");
                toReturn = doneException.getMessage();
            }
        } else if (commandComponents[0].equals("delete")) {
            try {
                int taskIndex = Integer.parseInt(commandComponents[1]) - 1;
                toReturn = deleteTask(taskIndex);
            } catch (IndexOutOfBoundsException e) {
                DeleteTaskException deleteException = new DeleteTaskException("The task number does not exist!");
                toReturn = deleteException.getMessage();
            } catch (NumberFormatException nfe) {
                DeleteTaskException deleteException =
                        new DeleteTaskException("Please provide a task number to delete!");
                toReturn = deleteException.getMessage();
            }
        } else if (commandComponents[0].equals("find")) {
            String keyword = commandComponents[1];
            toReturn = taskList.searchWithKeyword(keyword);
        } else {
            toReturn = getResultFromParser(nextInput);
        }
        return toReturn;
    }

    /**
     * Takes in user input via a Scanner object. For commands bye, done and delete this
     * method updates the TaskList object and writes the changes to the System via the Storage object.
     * The inputting of new tasks is passed to the Parser object. Thereafter the application's responses are printed.
     */
    public void handleUserInput() {
        Scanner sc = new Scanner(System.in);
        horiLine(60);
        System.out.println("  Hello! I'm IntelliGent!\n  What can I do for you?");
        horiLine(60);
        while (sc.hasNextLine()) {
            String nextInput = sc.nextLine();
            horiLine(60);
            System.out.println(getDukeResponse(nextInput));
            if (nextInput.equals("bye")) {
                sc.close();
                break;
            }
            horiLine(60);
        }

    }

}
