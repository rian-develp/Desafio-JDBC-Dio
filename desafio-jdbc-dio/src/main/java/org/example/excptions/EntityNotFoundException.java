package org.example.excptions;

public class EntityNotFoundException extends Exception{
    public EntityNotFoundException(String message){
        super(message);
    }
}
