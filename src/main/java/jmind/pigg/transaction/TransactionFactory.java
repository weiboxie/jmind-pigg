/*
 *  
 *
 * The jmind-pigg Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package jmind.pigg.transaction;

import javax.sql.DataSource;

import jmind.pigg.datasource.AbstractDataSourceFactory;
import jmind.pigg.operator.Pigg;
import jmind.pigg.transaction.exception.CannotCreateTransactionException;
import jmind.pigg.util.logging.InternalLogger;
import jmind.pigg.util.logging.InternalLoggerFactory;

import java.sql.Connection;
import java.util.List;

/**
 * @author xieweibo
 */
public abstract class TransactionFactory {

  private final static InternalLogger logger = InternalLoggerFactory.getInstance(TransactionFactory.class);

  public static Transaction newTransaction(Pigg pigg, String dataSourceFactoryName, TransactionIsolationLevel level) {
    DataSource dataSource = pigg.getMasterDataSource(dataSourceFactoryName);
    if (dataSource == null) {
      throw new IllegalArgumentException("Can't find master DataSource from pigg [" + pigg + "] " +
          "with datasource factory name [" + dataSourceFactoryName + "]");
    }
    return newTransaction(dataSource, level);
  }

  public static Transaction newTransaction(Pigg pigg, String dataSourceFactoryName) {
    return newTransaction(pigg, dataSourceFactoryName, TransactionIsolationLevel.DEFAULT);
  }

  public static Transaction newTransaction(String dataSourceFactoryName, TransactionIsolationLevel level) {
    Pigg piggs = Pigg.getInstance();  
    return newTransaction(piggs, dataSourceFactoryName, level);
  }

  public static Transaction newTransaction(String dataSourceFactoryName) {
    return newTransaction(dataSourceFactoryName, TransactionIsolationLevel.DEFAULT);
  }

  public static Transaction newTransaction(TransactionIsolationLevel level) {
    return newTransaction(AbstractDataSourceFactory.DEFULT_NAME, level);
  }

  public static Transaction newTransaction() {
    return newTransaction(AbstractDataSourceFactory.DEFULT_NAME, TransactionIsolationLevel.DEFAULT);
  }

  public static Transaction newTransaction(DataSource dataSource) {
    return newTransaction(dataSource, TransactionIsolationLevel.DEFAULT);
  }

  private static Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level) {
    if (dataSource == null) {
      throw new IllegalArgumentException("DataSource can't be null");
    }
    if (level == null) {
      throw new IllegalArgumentException("TransactionIsolationLevel can't be null");
    }
    ConnectionHolder connHolder = TransactionSynchronizationManager.getConnectionHolder(dataSource);
    return connHolder != null ?
        usingExistingTransaction(dataSource) :
        createNewTransaction(dataSource, level);
  }

  private static Transaction usingExistingTransaction(DataSource dataSource) {
    if (logger.isDebugEnabled()) {
      logger.debug("Using existing transaction");
    }
    Transaction transaction = new TransactionImpl(false, dataSource);
    return transaction;
  }

  private static Transaction createNewTransaction(DataSource dataSource, TransactionIsolationLevel expectedLevel) {
    if (logger.isDebugEnabled()) {
      logger.debug("Creating new transaction");
    }
    Connection conn = null;
    try {
      Integer previousLevel = null;
      boolean isMustRestoreAutoCommit = false;
      conn = dataSource.getConnection();
      if (logger.isDebugEnabled()) {
        logger.debug("Acquired Connection [" + conn + "] for JDBC transaction");
      }

      // 设置事务的隔离级别
      if (expectedLevel != TransactionIsolationLevel.DEFAULT) {
        previousLevel = conn.getTransactionIsolation();
        if (previousLevel != expectedLevel.getLevel()) {
          if (logger.isDebugEnabled()) {
            logger.debug("Changing isolation level of JDBC Connection [" + conn + "] to " +
                expectedLevel.getLevel());
          }
          conn.setTransactionIsolation(expectedLevel.getLevel());
        }
      }

      // 设置自动提交为false
      if (conn.getAutoCommit()) {
        isMustRestoreAutoCommit = true;
        if (logger.isDebugEnabled()) {
          logger.debug("Switching JDBC Connection [" + conn + "] to manual commit");
        }
        conn.setAutoCommit(false);
      }

      Transaction transaction = new TransactionImpl(true, dataSource, previousLevel, isMustRestoreAutoCommit);
      ConnectionHolder connHolder = new ConnectionHolder(conn);
      TransactionSynchronizationManager.bindConnectionHolder(dataSource, connHolder);
      return transaction;
    } catch (Throwable e) {
      DataSourceUtils.releaseConnection(conn, dataSource);
      throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", e);
    }
  }

}
