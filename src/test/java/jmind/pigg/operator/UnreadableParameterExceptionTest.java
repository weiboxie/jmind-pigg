package jmind.pigg.operator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheBy;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.SQL;
import jmind.pigg.binding.BindingException;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.support.DataSourceConfig;

/**
 * @author xieweibo
 */
public class UnreadableParameterExceptionTest {

  private final static Pigg pigg = Pigg.newInstance(DataSourceConfig.getDataSource());

  static {
    pigg.setLazyInit(true);
    pigg.setCacheHandler(new LocalCacheHandler());
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void test2() {
    thrown.expect(BindingException.class);
    thrown.expectMessage("Parameter ':1.c.d' can't be readable; caused by: There is no getter/setter for property named 'c' in 'class jmind.pigg.operator.UnreadableParameterExceptionTest$A'");
    Dao dao = pigg.create(Dao.class);
    dao.add2(new A());
  }

  @Test
  public void test3() {
    thrown.expect(BindingException.class);
    thrown.expectMessage("if use cache and sql has one in clause, property c of " +
        "class jmind.pigg.operator.UnreadableParameterExceptionTest$A " +
        "expected readable but not");
    Dao2 dao = pigg.create(Dao2.class);
    dao.gets(new ArrayList<Integer>());
  }

  @Test
  public void test4() {
    thrown.expect(BindingException.class);
    thrown.expectMessage("Parameter ':1' not found, available root parameters are []");
    Dao dao = pigg.create(Dao.class);
    dao.add();
  }

  @Test
  public void test5() {
    thrown.expect(BindingException.class);
    thrown.expectMessage("Parameter ':1' not found, available root parameters are []");
    Dao dao = pigg.create(Dao.class);
    dao.gets();
  }

  @DB
  static interface Dao {
    @SQL("insert into user(uid) values (:1.b.d)")
    public int add(A a);

    @SQL("insert into user(uid) values (:1.c.d)")
    public int add2(A a);

    @SQL("insert into user(uid) values(:1)")
    public int add();

    @SQL("select uid from user where uid in (:1)")
    public int[] gets();
  }

  @DB
  @Cache(prefix = "dao2_", expire = 100)
  static interface Dao2 {
    @SQL("select ... where c in (:1)")
    public List<A> gets(@CacheBy List<Integer> ids);
  }

  static class A {
    B b;

    public B getB() {
      return b;
    }
  }

  static class B {
    C c;

    public C getC() {
      return c;
    }
  }

  static class C {

  }

}
