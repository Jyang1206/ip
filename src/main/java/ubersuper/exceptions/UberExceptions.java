package ubersuper.exceptions;

/**
 * Runtime exception for user-visible validation and parsing errors in UberSuper.
 */

public class UberExceptions extends RuntimeException {

  /**
   * Creates an exception carrying a user-friendly message suitable for direct display.
   *
   * @param message explanation of the error to show to the user
   */

    public UberExceptions(String message) {
      super(message);
    }
}

