package jmind.pigg.jdbc.exception;

/**
 * @author xieweibo
 */
public class CannotSerializeTransactionException extends PessimisticLockingFailureException {

  public CannotSerializeTransactionException(String msg) {
    super(msg);
  }

  public CannotSerializeTransactionException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
