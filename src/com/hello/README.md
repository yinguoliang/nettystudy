# SEDA架构 
> Netty采用标准的SEDA(Staged Event-Driven Architecture)架构   
SEDA的核心思想是把一个**请求处理过程分成几个Stage**，  
不同资源消耗的Stag使用不同数量的线程来处理，  
Stag间使用事件驱动的异步通信模式。  
更进一步，在每个Stage中可以动态配置自己的线程数，  
在超载时降级运行或拒绝服务。   

# channel
> Netty对channel进行了增强  
> 增加的channel持有一个原生的SocketChannel对象   
> 增强的Channel自定义了pipeline来处理消息  
> 增强的channel实现了Unsafe类来进行消息的读取、渠道注册等功能   

# NIOEventLoop
* NioEventLoop是一个不断循环的代码   
  NioEventLoop持有一个selector对象  
  循环代码不断的监听selector事件，如果有感兴趣的事件出现，则转去处理事件
  