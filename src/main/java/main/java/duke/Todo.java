package main.java.duke;

public class Todo extends Task{

    String taskType = "T";

    /**
     * Creates an instance of Todo class
     * @param description
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation of the Todo object.
     * @return a string representation of the Todo object.
     */
    public String toString() {
        assert super.description.length() > 0 : "Description is empty";
        return "[" + taskType + "]" + "[" + super.getStatusIcon()
                + "] " + super.description;
    }

}
