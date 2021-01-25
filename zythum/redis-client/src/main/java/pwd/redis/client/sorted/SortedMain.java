package pwd.redis.client.sorted;

import redis.clients.jedis.Jedis;

/**
 * pwd.redis.client.sorted@gitbook
 *
 * <h1>TODO what you want to do?</h1>
 *
 * date 2021-01-25 18:38
 *
 * @author DingPengwei[dingpengwei@foxmail.com]
 * @version 1.0.0
 * @since DistributionVersion
 */
public class SortedMain {

    private static String host = "localhost";

    public static void main(String[] args) {
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis(host);
        // 如果 Redis 服务设置来密码，需要下面这行，没有就不需要
//        jedis.auth("123098");
        System.out.println("连接成功");
        //查看服务是否运行
        System.out.println("服务正在运行: " + jedis.ping());

        initData(jedis);



        jedis.close();
    }

    private static void initData(Jedis jedis){

        jedis.zadd("20200101",1,"id1" );
        jedis.zadd("20200101",2,"id2" );
        jedis.zadd("20200101",3,"id3" );
        jedis.zadd("20200101",4,"id4" );
        jedis.zadd("20200101",5,"id5" );
        jedis.zadd("20200101",60,"id6" );
        jedis.zadd("20200101",7,"id7" );


        jedis.zadd("20200102",1,"id1" );
        jedis.zadd("20200102",2,"id2" );
        jedis.zadd("20200102",3,"id3" );
        jedis.zadd("20200102",4,"id4" );
        jedis.zadd("20200102",5,"id5" );
        jedis.zadd("20200102",60,"id6" );
        jedis.zadd("20200102",7,"id7" );

        jedis.zadd("20200103",1,"id1" );
        jedis.zadd("20200103",2,"id2" );
        jedis.zadd("20200103",3,"id3" );
        jedis.zadd("20200103",4,"id4" );
        jedis.zadd("20200103",5,"id5" );
        jedis.zadd("20200103",60,"id6" );
        jedis.zadd("20200103",7,"id7" );

        jedis.zadd("20200104",1,"id1" );
        jedis.zadd("20200104",2,"id2" );
        jedis.zadd("20200104",3,"id3" );
        jedis.zadd("20200104",4,"id4" );
        jedis.zadd("20200104",5,"id5" );
        jedis.zadd("20200104",60,"id6" );
        jedis.zadd("20200104",7,"id7" );

        jedis.zadd("20200105",1,"id1" );
        jedis.zadd("20200105",2,"id2" );
        jedis.zadd("20200105",3,"id3" );
        jedis.zadd("20200105",4,"id4" );
        jedis.zadd("20200105",5,"id5" );
        jedis.zadd("20200105",60,"id6" );
        jedis.zadd("20200105",7,"id7" );
    }
}
