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


## Notes

- Good: I've observed services attempting to do a failed calculation again when a new order comes in.

## Ongoing problems

- WPS returns references as `xlin:href="http://localhost:8080/wps/RetrieveResultServlet?id=`, not as `xlin:href="http://riesgos-wps:8080/wps/RetrieveResultServlet?id=...`
    - Problem disappears after `docker compose -f docker-compose-with-wrappers.yml stop/start riesgos-wps`
    - This is a problem because wrapper cannot resolve that `localhost` link; causing bad data to be stored in the filestorage.
    - The problem only becomes evident when a downstream-service tries to access that data.

- Attempt to re-connect to wps from wrapper a few times if network-connection is shaky.


- It's possible for wrappers to start a process several times by accident. I just had deus being run thrice; with the following inputs:
    - [ { "id": 100, "product_type_id": 28, "name": "org.n52.gfz.riesgos.algorithm.impl.ShakygroundProcess output (100)" }, { "id": 95, "product_type_id": 27, "name": "org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess output (95)" }, { "id": 101, "product_type_id": 26, "name": "org.n52.gfz.riesgos.algorithm.impl.ModelpropProcess output (101)" } ]	106	org.n52.gfz.riesgos.algorithm.impl.DeusProcess output (106)	[]
    - Maybe wrappers don't check if an input-combination is currently being processed.


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





# Why does wps config only take effect after restart of container?

## 5. Finding where the error occurs
52N-Wps seems to be configured correctly.
If I run the request from (4) locally on the wps, adjusting the URL to localhost, I get a localhost-reference back.
Seems that the error might be in gfz-cli-tool.
Cannot find the phrase `localhost:8080` outside of a comment on that machine, though.

```xml
<Server port="8005" shutdown="SHUTDOWN">
  <Listener className="org.apache.catalina.startup.VersionLoggerListener" />
  <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />
  <GlobalNamingResources>
    <Resource name="UserDatabase" auth="Container" type="org.apache.catalina.UserDatabase" description="User database that can be updated and saved" factory="org.apache.catalina.users.MemoryUserDatabaseFactory" pathname="conf/tomcat-users.xml" />
  </GlobalNamingResources>
  <Service name="Catalina">
    <Connector port="8080" protocol="HTTP/1.1" connectionTimeout="180000" redirectPort="8443" />  
    <Engine name="Catalina" defaultHost="localhost">
      <Realm className="org.apache.catalina.realm.LockOutRealm">
        <Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase" />
      </Realm>
      <Host name="localhost" appBase="webapps" unpackWARs="true" autoDeploy="true">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs" prefix="localhost_access_log" suffix=".txt" pattern="%h %l %u %t &quot;%r&quot; %s %b" />
      </Host>
    </Engine>
  </Service>
</Server>/
```

## 4. Fixing the configuration for riesgos-wps
After restart of wps-server, still same problem.
Running the execute command from wrapper-container:
```
curl --location --request POST 'http://riesgos-wps:8080/wps/WebProcessingService?service=WPS&request=Execute&version=2.0.0&identifier=org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess' \
--header 'Content-Type: application/xml' \
--data-raw '<wps:Execute xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wps.xsd" response="document" mode="sync" service="WPS" version="2.0.0">
    <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.AssetmasterProcess</ows:Identifier>
    <wps:Input id="lonmin">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>-71.8</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="lonmax">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>-71.4</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="latmin">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>-33.2</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="latmax">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>-35.0</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="schema">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>SARA_v1.0</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="assettype">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>res</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="querymode">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>intersects</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="model">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>ValpCVTBayesian</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Output id="selectedRowsGeoJson" transmission="reference" mimeType="application/json" encoding="UTF-8"></wps:Output>
</wps:Execute>'
```
... yields the same result:
```xml
<wps:Result xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xlin="http://www.w3.org/1999/xlink" xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wps.xsd">
  <wps:JobID>98741347-d741-4b9e-b617-3f701316c76a</wps:JobID>
  <wps:Output id="selectedRowsGeoJson">
    <wps:Reference encoding="UTF-8" mimeType="application/json" xlin:href="http://localhost:8080/wps/RetrieveResultServlet?id=98741347-d741-4b9e-b617-3f701316c76aselectedRowsGeoJson.d94be155-7bfa-4e67-9b4e-ad7f5f9f271d"/>
  </wps:Output>
```
But when I replace `localhost` with `riesgos-wps` in the href, I do get that value from the wrapper-container.

## 3. Inspecting sent file:
http://filestorage:9000/riesgosfiles/C6646930D1E5AE7C191969E632DF323FBCEA6090 -> http://localhost/api/v1/files/C6646930D1E5AE7C191969E632DF323FBCEA6090
That file actually contains this:
`{"timestamp":"2023-01-13T17:02:53.101+00:00","status":404,"error":"Not Found","path":"/wps/RetrieveResultServlet"}`
When I execute, for example, assetmaster, I get as a response: 
```xml
<wps:JobID>708a26cf-fd24-4d7f-9c31-cc0124310f6b</wps:JobID>
<wps:Output id="selectedRowsGeoJson">
    <wps:Reference encoding="UTF-8" mimeType="application/json" xlin:href="http://localhost:8080/wps/RetrieveResultServlet?id=708a26cf-fd24-4d7f-9c31-cc0124310f6bselectedRowsGeoJson.5ed2f701-f81a-4199-bda3-9bd91bdd4fb0"/>
</wps:Output>
```
And indeed, that url cannot be resolved.
I think it should be `http://riesgos-wps:8080/wps/RetrieveResultServlet?id=708a26cf-fd24-4d7f-9c31-cc0124310f6bselectedRowsGeoJson.5ed2f701-f81a-4199-bda3-9bd91bdd4fb0`

## 2. Execute request sent:
```xml
<wps:Execute xmlns:wps="http://www.opengis.net/wps/2.0" xmlns:ows="http://www.opengis.net/ows/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wps/2.0 http://schemas.opengis.net/wps/2.0/wps.xsd" response="document" mode="sync" service="WPS" version="2.0.0">
    <ows:Identifier>org.n52.gfz.riesgos.algorithm.impl.shakemap_sampler</ows:Identifier>
    <wps:Input id="random_seed">
        <wps:Data mimeType="text/xml">
            <wps:LiteralValue>1</wps:LiteralValue>
        </wps:Data>
    </wps:Input>
    <wps:Input id="intensity_file">
        <wps:Reference xlink:href="http://filestorage:9000/riesgosfiles/C6646930D1E5AE7C191969E632DF323FBCEA6090" mimeType="text/xml"></wps:Reference>
    </wps:Input>
    <wps:Output id="intensity_output_file" transmission="reference" mimeType="text/xml" schema="http://earthquake.usgs.gov/eqcenter/shakemap" encoding="UTF-8"></wps:Output>
</wps:Execute>
```

## 1. Output from riesgos-wps
2023-01-13 17:02:53,776 [pool-7-thread-4] INFO  org.n52.wps.commons.context.ExecutionContextFactory: Context registered
2023-01-13 17:02:53,779 [pool-7-thread-4] WARN  org.n52.wps.server.request.InputHandler: Could not find class org.n52.gfz.riesgos.algorithm.impl.shakemap_sampler
2023-01-13 17:02:53,892 [pool-7-thread-4] INFO  org.n52.wps.server.request.InputHandler: Looking for matching Parser ... schema: "null", mimeType: "text/xml", encoding: "null"
2023-01-13 17:02:53,892 [pool-7-thread-4] INFO  org.n52.wps.io.ParserFactory: Matching parser found: org.n52.wps.io.datahandler.parser.GenericXMLDataParser@191033c5
2023-01-13 17:02:53,898 [pool-7-thread-4] ERROR org.n52.wps.io.datahandler.parser.GenericXMLDataParser: Could not parse inputstream as XMLObject.
org.apache.xmlbeans.XmlException: error: Unexpected element: CDATA
	at org.apache.xmlbeans.impl.store.Locale$SaxLoader.load(Locale.java:3511) ~[xmlbeans-2.6.0.jar:na]
	at org.apache.xmlbeans.impl.store.Locale.parseToXmlObject(Locale.java:1277) ~[xmlbeans-2.6.0.jar:na]
	at org.apache.xmlbeans.impl.store.Locale.parseToXmlObject(Locale.java:1264) ~[xmlbeans-2.6.0.jar:na]
	at org.apache.xmlbeans.impl.schema.SchemaTypeLoaderBase.parse(SchemaTypeLoaderBase.java:345) ~[xmlbeans-2.6.0.jar:na]
	at org.apache.xmlbeans.XmlObject$Factory.parse(XmlObject.java:688) ~[xmlbeans-2.6.0.jar:2.6.0-r1364789]
	at org.n52.wps.io.datahandler.parser.GenericXMLDataParser.parse(GenericXMLDataParser.java:61) [52n-wps-io-impl-4.0.0-beta.10.jar:na]
	at org.n52.wps.io.datahandler.parser.GenericXMLDataParser.parse(GenericXMLDataParser.java:40) [52n-wps-io-impl-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.InputHandler.handleComplexValueReference(InputHandler.java:1464) [52n-wps-server-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.InputHandler.<init>(InputHandler.java:228) [52n-wps-server-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.InputHandler.<init>(InputHandler.java:101) [52n-wps-server-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.InputHandler$Builder.build(InputHandler.java:134) [52n-wps-server-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:200) [52n-wps-server-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:71) [52n-wps-server-4.0.0-beta.10.jar:na]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) [na:1.8.0_342]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_342]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_342]
	at java.lang.Thread.run(Thread.java:750) [na:1.8.0_342]
Caused by: org.xml.sax.SAXParseException: Unexpected element: CDATA
	at org.apache.xmlbeans.impl.piccolo.xml.Piccolo.reportFatalError(Piccolo.java:1038) ~[xmlbeans-2.6.0.jar:na]
	at org.apache.xmlbeans.impl.piccolo.xml.Piccolo.parse(Piccolo.java:723) ~[xmlbeans-2.6.0.jar:na]
	at org.apache.xmlbeans.impl.store.Locale$SaxLoader.load(Locale.java:3479) ~[xmlbeans-2.6.0.jar:na]
	... 16 common frames omitted
2023-01-13 17:02:53,946 [pool-7-thread-4] INFO  org.n52.gfz.riesgos.algorithm.impl.shakemap_sampler: Cache-Hash: a6b30761e4f625456e556b001dad93cc
2023-01-13 17:02:53,946 [pool-7-thread-4] INFO  org.n52.gfz.riesgos.algorithm.impl.shakemap_sampler: There is no result in the cache
2023-01-13 17:02:55,213 [pool-7-thread-4] ERROR org.n52.gfz.riesgos.algorithm.impl.shakemap_sampler: Files could not be read
java.io.IOException: Error: No such container:path: 121ec5f4f1e6fc41c42e6f6a020f5d8714808b189b3421488c3ab4f519e9700d:/usr/share/git/shakemap_sampler/intensity_output_file.xml

	at org.n52.gfz.riesgos.cmdexecution.docker.DockerExecutionContextImpl.readFromFile(DockerExecutionContextImpl.java:148) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.readidatafromfiles.ReadSingleByteStreamFromPath.readFromFiles(ReadSingleByteStreamFromPath.java:73) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.readFromOutputFiles(BaseGfzRiesgosService.java:978) [gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutableInContext(BaseGfzRiesgosService.java:620) [gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutable(BaseGfzRiesgosService.java:540) [gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.run(BaseGfzRiesgosService.java:438) [gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.access$100(BaseGfzRiesgosService.java:405) [gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService.run(BaseGfzRiesgosService.java:310) [gfz-riesgos-wps.jar:na]
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:214) [52n-wps-server-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:71) [52n-wps-server-4.0.0-beta.10.jar:na]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) [na:1.8.0_342]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) [na:1.8.0_342]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) [na:1.8.0_342]
	at java.lang.Thread.run(Thread.java:750) [na:1.8.0_342]
2023-01-13 17:02:55,241 [pool-7-thread-4] ERROR org.n52.wps.server.request.ExecuteRequestV200: Exception/Error while executing ExecuteRequest for org.n52.gfz.riesgos.algorithm.impl.shakemap_sampler: org.n52.wps.server.ExceptionReport: Files could not be readError: No such container:path: 121ec5f4f1e6fc41c42e6f6a020f5d8714808b189b3421488c3ab4f519e9700d:/usr/share/git/shakemap_sampler/intensity_output_file.xml
java.io.IOException: Error: No such container:path: 121ec5f4f1e6fc41c42e6f6a020f5d8714808b189b3421488c3ab4f519e9700d:/usr/share/git/shakemap_sampler/intensity_output_file.xml

	at org.n52.gfz.riesgos.cmdexecution.docker.DockerExecutionContextImpl.readFromFile(DockerExecutionContextImpl.java:148)
	at org.n52.gfz.riesgos.readidatafromfiles.ReadSingleByteStreamFromPath.readFromFiles(ReadSingleByteStreamFromPath.java:73)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.readFromOutputFiles(BaseGfzRiesgosService.java:978)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutableInContext(BaseGfzRiesgosService.java:620)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutable(BaseGfzRiesgosService.java:540)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.run(BaseGfzRiesgosService.java:438)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.access$100(BaseGfzRiesgosService.java:405)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService.run(BaseGfzRiesgosService.java:310)
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:214)
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:71)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:750)

2023-01-13 17:02:55,244 [pool-7-thread-4] INFO  org.n52.wps.commons.context.ExecutionContextFactory: Context unregistered
2023-01-13 17:02:55,244 [http-nio-8080-exec-8] WARN  org.n52.wps.server.handler.RequestHandler: exception while handling ExecuteRequest.
2023-01-13 17:02:55,245 [http-nio-8080-exec-8] ERROR org.n52.wps.server.handler.RequestHandler: exception handling ExecuteRequest.
org.n52.wps.server.ExceptionReport: Files could not be readError: No such container:path: 121ec5f4f1e6fc41c42e6f6a020f5d8714808b189b3421488c3ab4f519e9700d:/usr/share/git/shakemap_sampler/intensity_output_file.xml
java.io.IOException: Error: No such container:path: 121ec5f4f1e6fc41c42e6f6a020f5d8714808b189b3421488c3ab4f519e9700d:/usr/share/git/shakemap_sampler/intensity_output_file.xml

	at org.n52.gfz.riesgos.cmdexecution.docker.DockerExecutionContextImpl.readFromFile(DockerExecutionContextImpl.java:148)
	at org.n52.gfz.riesgos.readidatafromfiles.ReadSingleByteStreamFromPath.readFromFiles(ReadSingleByteStreamFromPath.java:73)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.readFromOutputFiles(BaseGfzRiesgosService.java:978)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutableInContext(BaseGfzRiesgosService.java:620)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutable(BaseGfzRiesgosService.java:540)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.run(BaseGfzRiesgosService.java:438)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.access$100(BaseGfzRiesgosService.java:405)
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService.run(BaseGfzRiesgosService.java:310)
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:214)
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:71)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:750)

	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.readFromOutputFiles(BaseGfzRiesgosService.java:1005) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutableInContext(BaseGfzRiesgosService.java:620) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.runExecutable(BaseGfzRiesgosService.java:540) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.run(BaseGfzRiesgosService.java:438) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.access$100(BaseGfzRiesgosService.java:405) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService.run(BaseGfzRiesgosService.java:310) ~[gfz-riesgos-wps.jar:na]
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:214) ~[52n-wps-server-4.0.0-beta.10.jar:na]
	at org.n52.wps.server.request.ExecuteRequestV200.call(ExecuteRequestV200.java:71) ~[52n-wps-server-4.0.0-beta.10.jar:na]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[na:1.8.0_342]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[na:1.8.0_342]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[na:1.8.0_342]
	at java.lang.Thread.run(Thread.java:750) ~[na:1.8.0_342]
Caused by: java.io.IOException: Error: No such container:path: 121ec5f4f1e6fc41c42e6f6a020f5d8714808b189b3421488c3ab4f519e9700d:/usr/share/git/shakemap_sampler/intensity_output_file.xml

	at org.n52.gfz.riesgos.cmdexecution.docker.DockerExecutionContextImpl.readFromFile(DockerExecutionContextImpl.java:148) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.readidatafromfiles.ReadSingleByteStreamFromPath.readFromFiles(ReadSingleByteStreamFromPath.java:73) ~[gfz-riesgos-wps.jar:na]
	at org.n52.gfz.riesgos.algorithm.BaseGfzRiesgosService$InnerRunContext.readFromOutputFiles(BaseGfzRiesgosService.java:978) ~[gfz-riesgos-wps.jar:na]
	... 11 common frames omitted
