(```)
public class DBConnectionPool {

    //设置注册属性
    private String url = "jdbc:mysql://localhost:3306/vmaxtam";
    private String user = "root";
    private String password = "root";
    private static String driverClass = "com.mysql.jdbc.Driver";

    //设置连接池属性
    private int initSize = 5;
    private int maxSize = 8;

    //用LinkedList对象来保存connection对象
    public static LinkedList<Connection> connList = new LinkedList<Connection>();
    //声明一个临时变量来计算连接对象的数量
    private int currentsize = 0;

    //声明DBConnectionPool对象时自动注册驱动
    static {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //获取连接的方法
    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    //获取连接的方法
    private Connection getConnectionProxy() {

        try {
            //获取一个连接
            final Connection conn = DriverManager.getConnection(url, user, password);

            //把连接交给动态代理类转换为代理的连接对象
            Connection connection = (Connection) Proxy.newProxyInstance(
                DBConnectionPool.class.getClassLoader(),
                new Class[]{Connection.class},
                //编写一个方法处理器
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                        Object value = null;

                        //当遇到close方法，就会把对象放回连接池中，而不是关闭连接
                        if (method.getName().equals("close")) {
                            DBConnectionPool.connList.addLast(conn);
                        } else {
                            //其它方法不变
                            value = method.invoke(conn, args);
                        }
                        return value;
                    }
                }
            );
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //构造方法，初始化连接池，并往里面添加连接对象
    public DBConnectionPool() {
        for (int i = 0; i < initSize; i++) {
            Connection connection = this.getConnection();
            connList.add(connection);
            currentsize++;
        }
    }

    //获取连接池中的一个连接对象
    public Connection getConnFromPool() {
        //当连接池还没空
        if (connList.size() > 0) {
            Connection connection = connList.getFirst();
            connList.removeFirst();
            return connection;

        } else if (connList.size() == 0 && currentsize < 8) {
            //连接池被拿空，且连接数没有达到上限，创建新的连接
            currentsize++;
            connList.addLast(this.getConnection());

            Connection connection = connList.getFirst();
            connList.removeFirst();
            return connection;
        }

        throw new RuntimeException("连接数达到上限，请等待");
    }

    //把用完的连接放回连接池
    public void releaseConnection(Connection connection) {
        connList.addLast(connection);
    }


    public static void main(String[] args) {
        //获得连接池
        DBConnectionPool dbConnectionPool = new DBConnectionPool();

        /*从连接池中尝试获取9个连接
        for(int i = 0 ; i<9; i++){
            Connection conn = mypool.getConnFromPool();
            System.out.println(conn.toString());
        }*/

        //获取第五个连接后，释放一下，然后再获取
        for (int i = 0; i < 9; i++) {
            Connection conn = dbConnectionPool.getConnFromPool();
            if (i == 5) {
                dbConnectionPool.releaseConnection(conn);
            }
            System.out.println(conn.toString());
        }
    }

}
`
(```)
