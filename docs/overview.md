####ZooKeeper简介
ZooKeeper的目的，为分布式应用提供分布式的协同服务。

zk提供了一组原语，分布式系统可以根据这组原语构建更高级别的服务：比如同步、配置维护、组和命名。

构建正确的协同服务非常的困难，特别是那些资源竞争、死锁等情况，通过zk，分布式系统不需要从新开始构建协同服务。

zk提供的原语包含：

1.	create
2.	delete
3.	exists
4.	get data
5.	set data
6.	get chiledren
7.	sync

####设计目标
1.	足够简单：结构类似文件系统的（结点可以带数据，但是不能太大）
2.	冗余：保证可靠性
3.	顺序
4.	快：基于内存的操作

####保证

ZooKeeper保证了：

  1. 顺序一致性：客户端的操作会被按照顺序执行
  2. 原子性：操作要不失败要不成功
  3. 可靠性：一旦写入成功，数据就会被保持，直到下次覆盖。
  4. 实时性
  5. 单一系统镜像(single system image)：不管连接到zk集群的那台机器，客户端看到的视图都是一致的

####实现
ZooKeeper的组件包含：

![image](http://zookeeper.apache.org/doc/trunk/images/zkcomponents.jpg)

其中，Replicated Database是个内存数据库，保存了所有数据。

更新会被写到磁盘，以便恢复。写也会被先序列化到磁盘后，在应用到内存数据库中。

读的时候，会从各自server的内存数据库中读数据，写则是通过一致性协议完成（leader/follwer）的。

