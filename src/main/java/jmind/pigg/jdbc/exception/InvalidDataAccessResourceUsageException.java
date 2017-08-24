package jmind.pigg.jdbc.exception;

/**
 * 访问数据错误异常继承此类
 *
 * @author xieweibo
 */
public class InvalidDataAccessResourceUsageException extends NonTransientDataAccessException {

  public InvalidDataAccessResourceUsageException(String msg) {
    super(msg);
  }

  public InvalidDataAccessResourceUsageException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
