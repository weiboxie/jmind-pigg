package jmind.pigg.transaction;

/**
 * @author xieweibo
 */
@FunctionalInterface
public interface TransactionAction {

  void doInTransaction(TransactionStatus status);

}
