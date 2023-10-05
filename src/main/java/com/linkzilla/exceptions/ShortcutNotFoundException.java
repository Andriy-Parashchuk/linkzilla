package com.linkzilla.exceptions;

/**Exceptions for not found shortcuts for received long URL.*/
public class ShortcutNotFoundException extends Exception {

  public ShortcutNotFoundException(){}

  public ShortcutNotFoundException(String message){
    super(message);
  }

  public ShortcutNotFoundException(Throwable cause){
    super(cause);
  }

  public ShortcutNotFoundException(String message, Throwable cause){
    super(message, cause);
  }
}
