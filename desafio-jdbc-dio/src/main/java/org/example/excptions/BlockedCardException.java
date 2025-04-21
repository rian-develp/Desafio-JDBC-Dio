package org.example.excptions;

public class BlockedCardException extends Exception{
    public BlockedCardException(String message){
        super(message);
    }
}
