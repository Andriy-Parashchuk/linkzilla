package com.linkzilla.exceptions;

/**Exceptions for not found actual long URL for received shortcut.*/
public class LongUrlNotFoundException extends Exception{

  public LongUrlNotFoundException(){}

  public LongUrlNotFoundException(String message){
    super(message);
  }

  public LongUrlNotFoundException(Throwable cause){
    super(cause);
  }

  public LongUrlNotFoundException(String message, Throwable cause){
    super(message, cause);
  }
}