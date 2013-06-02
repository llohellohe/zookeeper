###创建ZooKeeper Session
可以通过两种方式，创建ZooKeeper的Session，不带sessionId和带sessionId(需要提供密码)

1.	ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
2.	ZooKeeper(String connectString, int sessionTimeout, Watcher watcher,boolean canBeReadOnly)
3.	ZooKeeper(String connectString, int sessionTimeout, Watcher watcher,long sessionId, byte[] sessionPasswd)
4.	ZooKeeper(String connectString, int sessionTimeout, Watcher watcher,long sessionId, byte[] sessionPasswd, boolean canBeReadOnly)

其中`Wather`接口定义了处理ZooKeeper事件的方法：
	
	abstract public void process(WatchedEvent event)
	
connectString类似:127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002/app/a 

###ZooKeeper Session初始化过程
1.	设置默认的Watcher
2.	通过ConnectStringParser将connectString解析成服务器列表，并根据列表创建HostProvider。
3.	创建ClientCnxn 
4.	调用ClientCnxn的start()方法，启动SendThread和EventThread。

创建SendThread和EventThread的时候，会设置`UncaughtExceptionHandler`，用于处理线程内的异常处理。

并且这两个线程会被设置为守护线程。



###ClientCnxn创建过程
1.	ClientCnxn(String chrootPath, HostProvider hostProvider, int sessionTimeout, ZooKeeper zooKeeper,ClientWatchManager watcher, ClientCnxnSocket clientCnxnSocket, boolean canBeReadOnly)
2.	ClientCnxn(String chrootPath, HostProvider hostProvider, int sessionTimeout, ZooKeeper zooKeeper,ClientWatchManager watcher, ClientCnxnSocket clientCnxnSocket,long sessionId, byte[] sessionPasswd, boolean canBeReadOnly)          
            
            
其中chrootPath为相对路径，如`127.0.0.1:3002/app/a`的相对路径为/app/a

HostProvider为zk服务器提供者。

ClientCnxnSocket是为了完成底层的数据通信(通过Socket)实现。

#####ClientCnxnSocket
可以通过设置`zookeeper.clientCnxnSocket`变量，设置ClientCnxnSocket的实现，默认为`ClientCnxnSocketNIO`，也可以选择Apache Netty的Client实现。
            
            
###SendThread
SendThread负责底层的通信，同时它将解析出Event信息，然后交给EventThread处理。            
###EventThread
EventThread是个守护线程，它使用`LinkedBlockingQueue`来保存等待处理的Events。

通过循环调用LinkedBlockingQueue的take()方法，或者Event。

如果获得的Event是eventOfDeath的话，则结束进程。

否则则处理该事件，并调用CallBack。
##EventThread和SendThread上面部分（**需要补充完善**）


 