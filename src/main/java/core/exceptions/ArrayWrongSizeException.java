package core.exceptions;

public class ArrayWrongSizeException extends Exception{

    public ArrayWrongSizeException(int expected_length, int received_length) {
        String message = "Invalid Array Size. Expected an Array with the Size of " + expected_length + ", but received an array with the size of " + received_length + " instead.";
        super(message);
    }

}
