# gateway
网关服务支持iphash负载均衡, 解决MCP SSE传输方式多节点部署session加载不正确问题

## 运行步骤
1. 启动 [eureka](https://github.com/shootercheng/eurkea-server) 服务
2. 启动 [GATEWAY](https://github.com/shootercheng/gateway)、[MCP-BOOT-WEBFLUX](https://github.com/shootercheng/mcp-spring-webflux)、 
[STREAMABLE-MCP-NO-SSE](https://github.com/shootercheng/streamable-mcp/tree/dev_eureka) 服务

## 负载均衡方法
1. GATEWAY -> RoundRobinLoadBalancer -> STREAMABLE-MCP-NO-SSE
2. GATEWAY -> IpHashLoadBalancer -> MCP-BOOT-WEBFLUX
