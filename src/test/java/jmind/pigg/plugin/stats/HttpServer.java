package jmind.pigg.plugin.stats;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.common.collect.Lists;

import jmind.pigg.annotation.Cache;
import jmind.pigg.annotation.CacheBy;
import jmind.pigg.annotation.CacheIgnored;
import jmind.pigg.annotation.DB;
import jmind.pigg.annotation.GeneratedId;
import jmind.pigg.annotation.SQL;
import jmind.pigg.annotation.Sharding;
import jmind.pigg.annotation.ShardingBy;
import jmind.pigg.crud.CrudRepository;
import jmind.pigg.operator.Pigg;
import jmind.pigg.operator.cache.LocalCacheHandler;
import jmind.pigg.sharding.ModTenTableShardingStrategy;
import jmind.pigg.support.DataSourceConfig;
import jmind.pigg.support.Randoms;
import jmind.pigg.support.Table;
import jmind.pigg.support.model4table.Msg;
import jmind.pigg.support.model4table.User;

/**
 * @author xieweibo
 */
public class HttpServer {

  private final static int PORT = 8080;

  public static void main(String[] args) throws Exception {
    init();

    Server server = new Server(PORT);

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    ServletHolder servlet = new ServletHolder(new PiggStatServlet());
    //servlet.setInitParameter("key", "9527");
    context.addServlet(servlet, "/pigg-stat");
    server.start();
    server.join();
  }

  private static void init() throws Exception {
    DataSource ds = DataSourceConfig.getDataSource();
    Table.USER.load(ds);
    Table.MSG.load(ds);
    Table.MSG_PARTITION.load(ds);
    Pigg pigg = Pigg.newInstance(ds);
    pigg.setLazyInit(true);
    pigg.setCacheHandler(new LocalCacheHandler() {
      void sleep() {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
        }
      }

      @Override
      public Object get(String key) {
        sleep();
        return super.get(key);
      }

      @Override
      public Map<String, Object> getBulk(Set<String> keys) {
        sleep();
        return super.getBulk(keys);
      }

      @Override
      public void set(String key, Object value, int expires) {
        sleep();
        super.set(key, value, expires);
      }

      @Override
      public void add(String key, Object value, int expires) {
        sleep();
        super.add(key, value, expires);
      }

      @Override
      public void batchDelete(Set<String> keys) {
        sleep();
        super.batchDelete(keys);
      }

      @Override
      public void delete(String key) {
        sleep();
        super.delete(key);
      }
    });
    final UserDao userDao = pigg.create(UserDao.class);
    final MsgDao msgDao = pigg.create(MsgDao.class);
    final CrudMsgDao crudMsgDao = pigg.create(CrudMsgDao.class);

    int id = 1;
    userDao.getIntegerId(id);
    userDao.getName(id);
    userDao.getBoolObjGender(id);

    int id1 = userDao.insertUser(createRandomUser());
    int id2 = userDao.insertUser(createRandomUser());
    crudMsgDao.save(Msg.createRandomMsg());
    crudMsgDao.save(Msg.createRandomMsg());
    List<Integer> ids = Lists.newArrayList(id1, id2);
    userDao.getUser(id1);
    userDao.getUser(id1);
    userDao.getUsers(ids);
    userDao.getUsers(ids);
    userDao.delete(id1);
    userDao.getUser(id1);
    userDao.delete(id1);
    userDao.getUsers(ids);
    userDao.deletes(ids);
    userDao.deletes2(ids);
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        int id = 1;
        userDao.getIntegerId(id);
        userDao.getName(id);
        userDao.getBoolObjGender(id);
        for (int i = 0; i < 1500; i++) {
          userDao.getLongObjMoney(id, null, null);
          msgDao.getMsgs(id);
          crudMsgDao.findAll();
          crudMsgDao.count();
        }
      }
    }, 0, 10, TimeUnit.SECONDS);
  }

  @DB(table = "user")
  @Sharding
  @Cache(prefix = "user", expire =100, cacheNullObject = true)
  static interface UserDao {

    @CacheIgnored
    @SQL("select id from #table where id = :1")
    public Integer getIntegerId(int id);

    @CacheIgnored
    @SQL("select name from #table where id = :1")
    public String getName(int id);

    @CacheIgnored
    @SQL("select gender from #table where id = :1")
    public Boolean getBoolObjGender(int id);

    @CacheIgnored
    @SQL("select money from #table where id = :1")
    public Long getLongObjMoney(int id, String str, List<User> users);

    @SQL("delete from #table where id = :1")
    public boolean delete(@CacheBy int id);

    @SQL("delete from #table where id = :1")
    public void deletes(@CacheBy List<Integer> ids);

    @SQL("delete from #table where id in (:1)")
    public void deletes2(@CacheBy List<Integer> ids);

    @SQL("select id, name, age, gender, money, update_time from #table where id = :1")
    public User getUser(@CacheBy int id);

    @SQL("select id, name, age, gender, money, update_time from #table where id in (:1)")
    public List<User> getUsers(@CacheBy List<Integer> ids);

    @GeneratedId
    @CacheIgnored
    @SQL("insert into user(name, age, gender, money, update_time) " +
        "values(:1.name, :1.age, :1.gender, :1.money, :1.updateTime)")
    public int insertUser(User user);

  }

  @DB(table = "msg")
  @Sharding(tableShardingStrategy = ModTenTableShardingStrategy.class)
  interface MsgDao {

    @SQL("select id, uid, content from #table where uid=:1")
    public List<Msg> getMsgs(@ShardingBy int uid);

  }

  @DB(table = "msg")
  interface CrudMsgDao extends CrudRepository<Msg, Integer> {
  }

  private static User createRandomUser() {
    Random r = new Random();
    String name = Randoms.randomString(20);
    int age = r.nextInt(200);
    boolean gender = r.nextBoolean();
    long money = r.nextInt(1000000);
    Date date = new Date();
    User user = new User(name, age, gender, money, date);
    return user;
  }

}
