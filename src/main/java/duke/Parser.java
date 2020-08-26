package main.java.duke;

import java.time.format.FormatStyle;

public class Parser {

    public Parser() {}

    public String dateTimeParser(String dateTime) {
        DateTimeConverter dtc = new DateTimeConverter(FormatStyle.MEDIUM, FormatStyle.SHORT);
        return dtc.processTime(dateTime);
    }

    public String[] memoTaskParser(String taskLine) {
        String[] tempType = taskLine.split(SpecialFormat.SPLIT_NOTN, 2);
        String[] tempDetails;

        if (tempType[0].equals("T")) {
            tempDetails = tempType[1].split(SpecialFormat.SPLIT_NOTN, 2);
            return new String[] {tempType[0], tempDetails[0], tempDetails[1]};
        } else {
            tempDetails = tempType[1].split(SpecialFormat.SPLIT_NOTN, 3);
            return new String[] {tempType[0], tempDetails[0], tempDetails[1], tempDetails[2]};
        }
    }

    public String[] commandParser(String input) {
        String commandType;
        String[] output = new String[] {};

        if (input.equals("bye")) {
            commandType = "bye";
            output = new String[] {commandType};
        } else if (input.equals("list")) {
            commandType = "list";
            output = new String[] {commandType};
        } else {
            String exceptionType;
            String[] inputSplitArr;
            inputSplitArr = input.split(" ", 2);
            commandType = inputSplitArr[0];

            if (commandType.equals("done") || commandType.equals("delete")) {
                try {
                    String actionNumber = inputSplitArr[1];
                    output = new String[] {commandType, actionNumber};
                } catch (Exception ex) {
                    commandType = "exception";
                    return new String[] {commandType, "empty_illegal"};
                }
            } else if (commandType.equals("deadline") || commandType.equals("event") ||
                        commandType.equals("todo")) {
                String taskContent;
                String dateTime;
                boolean exceptionAbsent = true;

                if (!commandType.equals("todo")) {
                    try {
                        inputSplitArr = inputSplitArr[1].split(
                                commandType.equals("event") ? " /at " : " /by ", 2);
                    } catch (Exception ex) {
                        exceptionAbsent = false;
                        exceptionType = commandType.equals("deadline")
                                ? "deadline"
                                : "event";
                        commandType = "exception";
                        return new String[] {commandType, exceptionType};
                    }
                }
                if (exceptionAbsent) {
                    if (commandType.equals("todo")) {
                        try {
                            taskContent = inputSplitArr[1];
                        } catch (Exception e) {
                            return new String[] {"exception", "todo"};
                        }
                        output = new String[] {commandType, taskContent};
                    } else {
                        try {
                            taskContent = inputSplitArr[0];
                            dateTime = inputSplitArr[1];
                            dateTime = this.dateTimeParser(dateTime);
                            output = new String[] {commandType, taskContent, dateTime};
                        } catch (Exception ex) {
                            exceptionAbsent = false;
                            exceptionType = commandType.equals("event")
                                    ? "event"
                                    : "deadline";
                            commandType = "exception";
                            return new String[] {commandType, exceptionType};
                        }
                    }
                }
            } else {
                exceptionType = "no_meaning";
                commandType = "exception";
                return new String[] {commandType, exceptionType};
            }
        }
        return output;
    }
}