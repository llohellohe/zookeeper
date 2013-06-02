
`Executor` 实现了`Wathcer`接口、`Runnable`接口、`DataMonitorListener`接口。

通过connectString 监视ZooKeeper的一个ZNode。

当ZNode发生事件变化时，通过process(WatchedEvent event)方法调用DataMonitor的process(WatchedEvent event)方法。

`DataMonitor`实现了`Wathcer`接口和`StatCallback`接口。

DataMonitor处理ZNode发生变化时的process，以及处理StatCallback的回调方法。

###Watcher接口
Watcher接口定义了process(WatchedEvent event) 方法

###WatchedEvent
WatchedEvent由KeeperState、EventType和path组成。

其中KeeperState分别为：

1.	Disconnected
2.	NoSyncConnected
3.	SyncConnected
4.	AuthFailed
5.	ConnectedReadOnly
6.	SaslAuthenticated
7.	Expired


EventType分别为：

1.	None
2.	NodeCreated
3.	NodeDeleted
4.	NodeDataChanged
5.	NodeChildrenChanged

###AsyncCallback接口
StatCallback接口是AsyncCallback的一种。

AsyncCallback一共定义了如下几个CallBack

1. DataCallback
2. ACLCallback
3. ChildrenCallback
4. StatCallback
5. StringCallback
6. VoidCallback
7. Children2Callback

###创建ZooKeeper Session

通过
 
	ZooKeeper(String connectString, int sessionTimeout, Watcher watcher)
	
就可以创建Zookeeper的一个Session。

