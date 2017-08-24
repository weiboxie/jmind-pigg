package jmind.pigg.transaction;

/**
 * @author xieweibo
 */
public interface TransactionAction {

  void doInTransaction(TransactionStatus status);

}
