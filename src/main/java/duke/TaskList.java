package duke;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskList {

    /**
     * Collection of Task objects for easy modification
     */
    protected List<Task> taskCollections;

    /**
     * Parser object to process input
     */
    protected Parser parser;

    /**
     * Directory of the file to store tasks
     */
    protected String memoFileDir;

    /**
     * Name of the file to store tasks
     */
    protected String memoFileName;


    /**
     * Constructor of TaskList class.
     * Initialize the taskCollections to be an empty ArrayList.
     *
     * @param taskCollections List of Task objects to initialize the class variable taskCollections.
     * @param memoFileDir     Directory of the file to store tasks.
     * @param memoFileName    Name of the file to store tasks.
     */
    public TaskList(List<Task> taskCollections, String memoFileDir, String memoFileName) {
        this.taskCollections = taskCollections;
        this.parser = new Parser();
        this.memoFileDir = memoFileDir;
        this.memoFileName = memoFileName;
    }


    /**
     * Another Constructor of TaskList class.
     * Initialize the taskCollections to be an empty ArrayList.
     *
     * @return Type, status, content, and empty datetime of Task object in the form of an array of Strings
     */
    public TaskList() {
        this.taskCollections = new ArrayList<>();
    }


    /**
     * Return taskCollections of the TaskList object.
     *
     * @return Current list of Tasks.
     */
    public List<Task> showList() {
        return taskCollections;
    }


    /**
     * Return List of the Task objects matching the keyword.
     *
     * @param keyword  User input of keyword to look for.
     * @return Matching Task objects.
     */
    public List<Task> searchTask(String keyword) {
        List<Task> searchResult = new ArrayList<>();
        Iterator itr = taskCollections.iterator();
        String keywordLowerCase = keyword.toLowerCase();

        while (itr.hasNext()) {
            Task currTask = (Task) itr.next();
            if (currTask.getDescription().toLowerCase().contains(keywordLowerCase)) {
                searchResult.add(currTask);
                continue;
            }
            if (currTask.getType().equals("D") || currTask.getType().equals("E")) {
                if (currTask.getInfo()[3].toLowerCase().contains(keywordLowerCase)) {
                    searchResult.add(currTask);
                }
            }
        }

        return searchResult;
    }

    
    /**
     * Add new Task object to the temporary collection and local memory.
     */
    public List<String> addTask(String[] commandArr) {
        String type = commandArr[0];
        String taskContent = commandArr[1];
        List<String> output = new ArrayList<>();

        Task t = type.equals("todo")
                ? new Todo(taskContent)
                : type.equals("event")
                ? new Event(taskContent, commandArr[2])
                : new Deadline(taskContent, commandArr[2]);
        try {
            taskCollections.add(t);
            new Storage(memoFileDir, memoFileName).appendToFile(memoFileDir + memoFileName, t);
            output.add("Got it. I've added ths task:\n" + "  " + taskCollections.get(taskCollections.size() - 1)
                    + "\nNow you have " + taskCollections.size() + " tasks in the list.");
        } catch (Exception e) {
            output.addAll(HandleException.handleException(
                    DukeException.ExceptionType.READ_FILE));
        }
        return output;
    }


    /**
     * Modify temporary tasklist and overwrite local memory.
     */
    public List<String> editTask(String[] commandArr) {
        List<String> output = new ArrayList<>();

        try {
            String type = commandArr[0];
            int taskNumber = Integer.parseInt(commandArr[1]);
            int actionNumber = taskNumber - 1;
            String taskContent = taskCollections.get(actionNumber).toString();

            editTaskCollections(type, actionNumber);
            new Storage(memoFileDir, memoFileName).write_memory(taskCollections);
            output.add(addSuccessMsg(type, taskContent));

        } catch (Exception ex) {
            output.addAll(HandleException.handleException(
                    DukeException.ExceptionType.EMPTY_ILLEGAL));
        }
        return output;
    }


    public void editTaskCollections(String type, int actionNumber) {
        switch (type) {
        case "delete":
            taskCollections.remove(actionNumber);
            break;
        case "done":
        default:
            taskCollections.get(actionNumber).markAsDone();
            break;
        }
    }


    public String addSuccessMsg(String type, String taskContent) {
        if (type.equals("delete")) {
            return "Noted. I've removed this task:\n" + taskContent + "\n"
                    + "Now you have " + taskCollections.size() + " tasks in the list.";
        }
//        return "Nice! I've marked this task as done:\n" + " [\u2713] "
//                + taskCollections.get(actionNumber).toString().split("] ", 2)[1];
        return "Nice! I've marked this task as done:\n" + " [\u2713] "
                + taskContent.split("] ", 2)[1];
    }

}
