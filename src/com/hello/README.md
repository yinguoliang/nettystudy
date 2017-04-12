## SEDA架构 
> Netty采用标准的SEDA(Staged Event-Driven Architecture)架构   
SEDA的核心思想是把一个**请求处理过程分成几个Stage**，  
不同资源消耗的Stag使用不同数量的线程来处理，  
Stag间使用事件驱动的异步通信模式。  
更进一步，在每个Stage中可以动态配置自己的线程数，  
在超载时降级运行或拒绝服务。   
