# Tips


## Docker

- When in office: Cable in, VPN off, Wifi off. Otherwise connection-problems between the docker-containers.
- It takes a while for first websocket-connections to be established.

## Compiling

- Building wrapper
    cd asyncwrapper
    ./mvn_package.sh


## Services

- Accessing db from host-machine
    - psql -h 0.0.0.0 -p 5432 -U postgres (pw: postgres)

- Empty all tables:
    - truncate order_job_refs, orders, complex_outputs, complex_outputs_as_inputs, complex_inputs, complex_inputs_as_values, literal_inputs, bbox_inputs, jobs, processes;

- localhost:8081 -> Pulsar
- localhost:8082/wps
- localhost:8082/manager
- localhost:8082/geoserver
- localhost:9090 -> filestorage


Getting products:
postgres=# 
select co.wps_identifier, co.link, p.wps_identifier, ojr.order_id 
    from complex_outputs as co 
    join jobs as j on co.job_id = j.id 
    join processes as p on j.process_id = p.id 
    join order_job_refs as ojr on j.id = ojr.job_id;
    
   wps_identifier    |                                     link                                      |                    wps_identifier                     | order_id 
---------------------+-------------------------------------------------------------------------------+-------------------------------------------------------+----------
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        1
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        2
 selectedRows        | http://filestorage:9000/riesgosfiles/7B9EE041AB9C14EED5787BB37D8DBDFADA33DA0C | org.n52.gfz.riesgos.algorithm.impl.QuakeledgerProcess |        2
 selectedRows        | http://filestorage:9000/riesgosfiles/605E2A910BB53A241F70D7A515E1D227884B90E1 | org.n52.gfz.riesgos.algorithm.impl.QuakeledgerProcess |        2
 shakeMapFile        | http://filestorage:9000/riesgosfiles/B312B26FBAE05197046986FD1148E9D6DB7D10DA | org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess |        2
 shakeMapFile        | http://filestorage:9000/riesgosfiles/6A06F49A7996BEC897729CDBE88539A23526F9D0 | org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess |        2
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        3
 selectedRows        | http://filestorage:9000/riesgosfiles/542D7AC160040F79AC93451BC6133257E3EB3871 | org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess   |        4
 selectedRowsGeoJson | http://filestorage:9000/riesgosfiles/51768D5104799C07C72250C9F8E37853D4300B1D | org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess |        4



## Ongoing problems

- WPS returns references as `xlin:href="http://localhost:8080/wps/RetrieveResultServlet?id=`, not as `xlin:href="http://riesgos-wps:8080/wps/RetrieveResultServlet?id=...`
    - Problem disappears after `docker compose -f docker-compose-with-wrappers.yml stop/start riesgos-wps`
    - This is a problem because wrapper cannot resolve that `localhost` link; causing bad data to be stored in the filestorage.
    - The problem only becomes evident when a downstream-service tries to access that data.

- Attempt to re-connect to wps from wrapper a few times if network-connection is shaky.

- It's possible for wrappers to start a process several times by accident. I just had deus being run thrice; with the following inputs:
    - [ { "id": 100, "product_type_id": 28, "name": "org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess output (100)" }, { "id": 95, "product_type_id": 27, "name": "org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess output (95)" }, { "id": 101, "product_type_id": 26, "name": "org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess output (101)" } ]	106	org.n52.gfz.riesgos.algorithm.impl.DeusProcess output (106)	[]
    - Maybe wrappers don't check if one input-combination is currently being processed.


- On smaller machines, lot's of OutOfMemoryError: java.lang.HeapSize
  - Changing CATALINA_OPTS did not help on my 16GB. 
  - https://stackoverflow.com/questions/29923531/how-to-set-java-heap-size-xms-xmx-inside-docker-container


- When not OOME, sometimes concurrent-write-error from wps:
    ```
    async-assetmaster_wrapper-1  | 2023-01-16 08:10:34,781 [assetmaster-asyncwrapper_new-order_subscription] INFO  AbstractWrapper: Added job to order
    async-assetmaster_wrapper-1  | 2023-01-16 08:10:34,782 [assetmaster-asyncwrapper_new-order_subscription] INFO  AbstractWrapper: Start mapping to wps inputs
    async-assetmaster_wrapper-1  | 2023-01-16 08:10:35,029 [assetmaster-asyncwrapper_new-order_subscription] INFO  org.n52.geoprocessing.wps.client.WPSClientSession: CONNECT
    async-assetmaster_wrapper-1  | 2023-01-16 08:10:42,772 [assetmaster-asyncwrapper_new-order_subscription] INFO  AbstractWrapper: WPS call failed
    async-assetmaster_wrapper-1  | 2023-01-16 08:10:42,773 [assetmaster-asyncwrapper_new-order_subscription] INFO  AbstractWrapper: WPS call failed because of: Got HTTP error code, response: Exceptions: [      Code: NoApplicableCode   Text: java.util.ConcurrentModificationException: Document changed during save
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver.process(Saver.java:302)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$TextSaver.write(Saver.java:1817)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver.ensure(Saver.java:2521)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver.access$100(Saver.java:2419)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver$OutputStreamImpl.read(Saver.java:2570)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver.read(Saver.java:2504)
    async-assetmaster_wrapper-1  |  at java.io.InputStream.read(InputStream.java:101)
    async-assetmaster_wrapper-1  |  at org.apache.commons.io.IOUtils.copyLarge(IOUtils.java:1262)
    async-assetmaster_wrapper-1  |  at org.apache.commons.io.IOUtils.copy(IOUtils.java:1236)
    async-assetmaster_wrapper-1  |  at org.n52.wps.server.handler.RequestHandler.handle(RequestHandler.java:431)
    async-assetmaster_wrapper-1  |  at org.n52.wps.server.WebProcessingService.doGet(WebProcessingService.java:223)
    async-assetmaster_wrapper-1  |  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    async-assetmaster_wrapper-1  |  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    async-assetmaster_wrapper-1  |  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    async-assetmaster_wrapper-1  |  at java.lang.reflect.Method.invoke(Method.java:498)
    async-assetmaster_wrapper-1  |  at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:221)
    async-assetmaster_wrapper-1  |  at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:137)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:110)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandleMethod(RequestMappingHandlerAdapter.java:776)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:705)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:85)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:959)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:893)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:966)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:857)
    async-assetmaster_wrapper-1  |  at javax.servlet.http.HttpServlet.service(HttpServlet.java:655)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:842)
    async-assetmaster_wrapper-1  |  at javax.servlet.http.HttpServlet.service(HttpServlet.java:764)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:207)
    async-assetmaster_wrapper-1  |  at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:176)
    async-assetmaster_wrapper-1  |  at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:344)
    async-assetmaster_wrapper-1  |  at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:261)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at com.thetransactioncompany.cors.CORSFilter.doFilter(CORSFilter.java:169)
    async-assetmaster_wrapper-1  |  at com.thetransactioncompany.cors.CORSFilter.doFilter(CORSFilter.java:232)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:197)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:135)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:687)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:360)
    async-assetmaster_wrapper-1  |  at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:399)
    async-assetmaster_wrapper-1  |  at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
    async-assetmaster_wrapper-1  |  at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:890)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1789)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
    async-assetmaster_wrapper-1  |  at java.lang.Thread.run(Thread.java:750)        Locator: null]
    async-assetmaster_wrapper-1  | org.n52.geoprocessing.wps.client.WPSClientException: Got HTTP error code, response: Exceptions: [        Code: NoApplicableCode  Text: java.util.ConcurrentModificationException: Document changed during save
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver.process(Saver.java:302)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$TextSaver.write(Saver.java:1817)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver.ensure(Saver.java:2521)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver.access$100(Saver.java:2419)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver$OutputStreamImpl.read(Saver.java:2570)
    async-assetmaster_wrapper-1  |  at org.apache.xmlbeans.impl.store.Saver$InputStreamSaver.read(Saver.java:2504)
    async-assetmaster_wrapper-1  |  at java.io.InputStream.read(InputStream.java:101)
    async-assetmaster_wrapper-1  |  at org.apache.commons.io.IOUtils.copyLarge(IOUtils.java:1262)
    async-assetmaster_wrapper-1  |  at org.apache.commons.io.IOUtils.copy(IOUtils.java:1236)
    async-assetmaster_wrapper-1  |  at org.n52.wps.server.handler.RequestHandler.handle(RequestHandler.java:431)
    async-assetmaster_wrapper-1  |  at org.n52.wps.server.WebProcessingService.doGet(WebProcessingService.java:223)
    async-assetmaster_wrapper-1  |  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    async-assetmaster_wrapper-1  |  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    async-assetmaster_wrapper-1  |  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    async-assetmaster_wrapper-1  |  at java.lang.reflect.Method.invoke(Method.java:498)
    async-assetmaster_wrapper-1  |  at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:221)
    async-assetmaster_wrapper-1  |  at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:137)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:110)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandleMethod(RequestMappingHandlerAdapter.java:776)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:705)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:85)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:959)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:893)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:966)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:857)
    async-assetmaster_wrapper-1  |  at javax.servlet.http.HttpServlet.service(HttpServlet.java:655)
    async-assetmaster_wrapper-1  |  at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:842)
    async-assetmaster_wrapper-1  |  at javax.servlet.http.HttpServlet.service(HttpServlet.java:764)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:207)
    async-assetmaster_wrapper-1  |  at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:176)
    async-assetmaster_wrapper-1  |  at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:344)
    async-assetmaster_wrapper-1  |  at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:261)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at com.thetransactioncompany.cors.CORSFilter.doFilter(CORSFilter.java:169)
    async-assetmaster_wrapper-1  |  at com.thetransactioncompany.cors.CORSFilter.doFilter(CORSFilter.java:232)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:189)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:162)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:197)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:541)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:135)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:687)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)
    async-assetmaster_wrapper-1  |  at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:360)
    async-assetmaster_wrapper-1  |  at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:399)
    async-assetmaster_wrapper-1  |  at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
    async-assetmaster_wrapper-1  |  at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:890)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1789)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
    async-assetmaster_wrapper-1  |  at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
    async-assetmaster_wrapper-1  |  at java.lang.Thread.run(Thread.java:750)        Locator: null]
    async-assetmaster_wrapper-1  |  at org.n52.geoprocessing.wps.client.WPSClientSession.retrieveResponseOrExceptionReportInpustream(WPSClientSession.java:564)
    async-assetmaster_wrapper-1  |  at org.n52.geoprocessing.wps.client.WPSClientSession.retrieveCapsViaGET(WPSClientSession.java:536)
    async-assetmaster_wrapper-1  |  at org.n52.geoprocessing.wps.client.WPSClientSession.connect(WPSClientSession.java:170)
    async-assetmaster_wrapper-1  |  at org.n.riesgos.asyncwrapper.process.wps.WPSClientService.establishWPSConnection(WPSClientService.kt:17)
    async-assetmaster_wrapper-1  |  at org.n.riesgos.asyncwrapper.dummy.AbstractWrapper.runOneJob(AbstractWrapper.kt:335)
    async-assetmaster_wrapper-1  |  at org.n.riesgos.asyncwrapper.dummy.AbstractWrapper.fillConstraintsAndRun(AbstractWrapper.kt:262)
    async-assetmaster_wrapper-1  |  at org.n.riesgos.asyncwrapper.dummy.AbstractWrapper.run(AbstractWrapper.kt:57)
    async-assetmaster_wrapper-1  |  at org.n.riesgos.asyncwrapper.events.OrderMessageHandler.handleMessage(OrderMessageHandler.kt:23)
    async-assetmaster_wrapper-1  |  at org.n.riesgos.asyncwrapper.pulsar.PulsarConsumer.receiveMessages(PulsarConsumer.kt:22)
    async-assetmaster_wrapper-1  |  at org.n.riesgos.asyncwrapper.pulsar.PulsarConsumer.run(PulsarConsumer.kt:13)
    async-assetmaster_wrapper-1  |  at java.base/java.lang.Thread.run(Thread.java:831)
    async-assetmaster_wrapper-1  | acknowledge message
    async-assetmaster_wrapper-1  | 2023-01-16 08:10:47,280 [pulsar-timer-5-1] INFO  org.apache.pulsar.client.impl.ConsumerStatsRecorderImpl: [new-order] [assetmaster-asyncwrapper_new-order_subscription] [b02f1] Prefetched messages: 0 --- Consume throughput received: 0.02 msgs/s --- 0.00 Mbit/s --- Ack sent rate: 0.02 ack/s --- Failed messages: 0 --- batch messages: 0 ---Failed acks: 0
    ```


