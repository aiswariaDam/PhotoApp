# PhotoApp
This is a Microservice application

This is Microservice tutorial. People who are interested to learn Microservices can refer this tutorial.

Microservices

Spring Boot Microservices and Spring Cloud

Microservices are Small RESTfull services designed to fulfil one service. 
Configured to work on cloud 

Scalability -  developers can scale the application easily
	Can add new features easy
Agility - agile 
	Without rebuilding the entire code new features can be introduced
Hybrid technologies -
	Depending upon requirement individual components of the application can be developed in different technologies
Fault tolerence 
	- when there is some issue one component entire application execution fill fail

Eureka Discovery Server 
- Use to the Scale up application 
￼


@EnableEurekaServer - to make a springbok application a eureka server 

Ribbon - A load balancer from Netflix family , which can be used to balance load with running micro services, while scale up 
￼
Zuul API Gateway is the again from Netflix family 
-> it has Ribbon Load Balancer in build in it

Zuul has Spring security enabled in it, which provides authentication 

Registering Multiple instance of Same Application in Discovery Server
1. Start the application again with using normal start procedure. (Since we set Zero as port no on application.properties server will assign new port no.s each time. When port no is hardcoded we can’t do this way)
A new instance will be created but we can find only one in Discovery server
To fix this we need to make some config changes in application.properties

2.	We need to set Instance ID in application properties to make each instance unique
eureka.instance.instance-id=${spring.application.name}:${spring.application.instance_id:${random.value}}

Where spring.application.instance_id is a value we should pass a command line argument while starting server 
Random.value is a no randomly generated 

We can also set the port no also from command line argument 

Spring Cloud 
In  spring microservices architecture Spring cloud acts as a config server 
We can place our common properties in one application file and put that in a GIT repository 
Thought Spring cloud we can access this config file 
    - Spring will load this application properties only during server start. 
    - So when we need to make some change in application  properties we need to start all the serves , which is not a good idea 
    - Thus we use SPRING CLOUD BUS
    - 
Instead of storing config file in GIT we can use a native file as config file 
spring.profiles.active=native
    - When we want to set properties specific for a micro service , then we want to create a properties file in the name of that specific micro service at the file location,
    - And in the bootstrap file for that specific micro service we should give change the file name to required name 
spring.cloud.config.name=PhotoAppConfigServer. -> spring.cloud.config.name=customer-ws

spring.profiles.active=native
spring.cloud.config.server.native.search-locations=file://${user.home}/Aish/Work/MicroServiceProject/


We can find below line in the server start up logs 

2020-12-02 06:17:27.814  INFO 37586 --- [  restartedMain] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='configService', propertySources=[MapPropertySource {name='file:///Users/aiswariadamodaran/Aish/Work/MicroServiceProject/customer-ws.properties'}, MapPropertySource {name='file:///Users/aiswariadamodaran/Aish/Work/MicroServiceProject/application.properties'}]}

- To get these in the postman use URL 
http://localhost:8012/customer-ws/default,  to get the common properties file -> http://localhost:8012/PhotoAppConfigServer/native 
    - 
Spring Actuator 
    - Used to monitor and manage spring boot application 
    - /health - to know whether application is up and running, how much disk space it takes etc 
    - /beans list of beans in the micro service 
    - /httptrace last 100 request-response details 
    - Some of them are not safe to expose 
management.endpoint.web.exposure.include=health,beans,routes,httptrace
- Google to find the api ’s which are present by default and which one to configure 
- We have to add actuator dependency in Zuul project and make confidence changes in webSecurity 

To Encrypt we can we Java Cryptography Extension (JCE)  this is a library which we can download and add to Java-> lib then 
We should add encryption key to bootstrap.properties of config server 
encrypt.key=hui454nkn
We can use config server url to encrypt any string 
http://localhost:8012/encrypt

Now we can place these encrypted values in properties file 
For decrypting the values from properties file it should be in a format 
spring.datasource.password={cipher}encrypted value

Rest Template 

Feign Client -  this is an API which Spring provides ,
- Feign abstract all the details of calling an API
- With Feign we have to add dependency in pom and give some additional annotations
- @FeignClients in our Spring main class
- Create an interface with FeignClient annotation, we need not implement this interface, this will be done by Feign Archetecture 
@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {

    @GetMapping("/users/{id}/albums")
    public List<AlbumResponseModel> getAlbums(@PathVariable String id);
}

Logging the req and response and error msg, etc is also made easy with Feign Client 

FeignErrorDecoder  - we can implement this interface which feign provides to handle exception 
- We can handle each error scenario and put custom exception using this 
- Provide one single place to handle all exceptions 
- 


Hystrix
Circuit breaker 
- In case if A micro service is not available, Hystrix will navigate our call to the Dummy implementation 
@FeignClient(name = "albums-ws", fallback = AlbumsFallBack.class)
public interface AlbumsServiceClient {

    @GetMapping("/users/{id}/albums")
    public List<AlbumResponseModel> getAlbums(@PathVariable String id);
}

@Component
class AlbumsFallBack implements  AlbumsServiceClient{

    @Override
    public List<AlbumResponseModel> getAlbums(String id) {
        return null;
    }
}


Sleuth and Zipkin 
This is used for Logging the request details between micro services 
Trace id and span id will be generated which can be used for tracing request details
Zipkin server can be used to plot the request details with TraceId
￼


2020-12-10 14:14:03.985 DEBUG [customer-ws,16e91cce06ac1afa,192f3ff43582a0c8,true] 28024 --- [rix-albums-ws-1] c.p.user.api.data.AlbumsServiceClient    : [AlbumsServiceClient#getAlbums] [{"albumId":"album1Id","userId":"79f81a0c-6e53-43fe-8c38-830d6a6e2497","name":"album 1 name","description":"album 1 description"},{"albumId":"album2Id","userId":"79f81a0c-6e53-43fe-8c38-830d6a6e2497","name":"album 2 name","description":"album 2 description"}]
2020-12-10 14:14:03.986 DEBUG [customer-ws,16e91cce06ac1afa,192f3ff43582a0c8,true] 28024 --- [rix-albums-ws-1] c.p.user.api.data.AlbumsServiceClient    : [AlbumsServiceClient#getAlbums] <--- END HTTP (259-byte body)
2020-12-10 14:14:04.692  INFO [customer-ws,,,] 28024 --- [erListUpdater-0] c.netflix.config.ChainedDynamicProperty  : Flipping property: albums-ws.ribbon.ActiveConnectionsLimit to use NEXT property: niws.loadbalancer.availabilityFilteringRule.activeConnectionsLimit = 2147483647

16e91cce06ac1afa, - > TraceId
192f3ff43582a0c8, - > spanId
true - > should be sent to zipkin server 

Zipkin Dashboard 
Zipkin provides a dashboard where we can search with traceId and see the details of a transaction. 
We can even save it as JSON. 
￼


When we need to enable Spring security in Eureka discovery, we need to add WebSecurity class in the Root package, of  discovery project. 

High Level Security
Even though Zuul API  is secured we can provide security to each micro services separately. 
By default Header is blocked from Zuul to pass to Microservices, first we have to enable header to be pass through Zuul and reach Microservices,

-> we have to add web security to each microsevices and enable AuthFilter in MicroService and add it to WebSecurity class. 
-> we can also provide role based authentication in Microservices. 
-> Use annotation on the method to enable role based access
@Secured 
@PreAuthorize
@PostAuthorize

Deploy MicroService On Docker and AMS
EC2 - Elastic Compute Cloud 

Docker 

To build a docker image - first start the docker, double click on docker on applications , other wise it will show exception as docker demon not present 

Create a docker file inside the project folder. 

FROM openjdk:8-jdk-alpine
COPY target/PhotoAppDiscoveryService-0.0.1-SNAPSHOT.jar PhotoAppDiscoveryService.jar
ENTRYPOINT ["java", "-jar", "PhotoAppDiscoveryService.jar"]

EntryPoint the command to start application 

Then open terminal , to go to location where docker file is placed. Run the command
docker build --tag=eureka-server --force-rm=true .

List docker images - > docker images
Docker images which are in running state - > docker ps -a 
Run docker -> docker run --name photo-app-eureka-server -p 8090:8015 -p 8090:8016 eureka-server

To stop docker -> docker stop imageId
To delete docker images -> docker rm imageId 

AMI - > Amazon Machine Image



