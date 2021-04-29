# Spring Mvc 

***

## 목차
[1. 프론트 컨트롤러 패턴](#프론트-컨트롤러-패턴) <br/>
[2. Jar vs War](#Jar-vs-War) <br/> 
[3. Lombok 설정](#Lombok-설정) <br/> 
[4. @RestController vs @Controller](#@RestController-vs-@Controller) <br/>
[5. 로깅](#로깅) <br/> 
[6. 요청 매핑](#요청-매핑) <br/> 
[7. 요청 매핑 API 예시](#요청-매핑-API-예시) <br/> 
[8. HTTP 요청 기본 헤더 조회](#HTTP-요청-기본-헤더-조회) <br/> 
***


## 프론트 컨트롤러 패턴 

공통 처리나 반복적인 행위들을 해줄 수 있는 프론트 컨트롤러가 Spring Mvc에서 필요하다. 이 프론트 컨트롤러가 실행되고 난 후 각 요청에 맞는 세부적인 컨트롤러를 호출하는 방식으로 동작한다. 뭐 예를들면 뷰를 렌더링 해주는 것들이 있다. 프론트 컨트롤러가 없다면 매 컨트롤러마다 이런 공통 로직들을 다 입력해줘야한다. 

spring-web-mvc 에서 이런 프론트 컨트롤러의 역할을 해주는게 DispatcherServlet 이다.  

***

## Jar vs War 

Jar를 사용하면 항상 내장 서버인 톰캣을 사용하고 (톰캣이 아닌 다른 서버를 사용할 수 있습니다.) 최적화되어 있기 때문에 더 편하다. War는 주로 외부 서버에 배포하는 목적으로 사용된다. 

***

## Lombok 설정

Preference -> Build, Execution, Deployment -> Compiler -> Annotation Processors 에 Enable annotation processing 활성화를 눌러야 롬북을 적용시킬 수 있다. 

***

## @RestController vs @Controller 

@Controller 는 반환 값이 String 인 경우에 뷰를 찾고 렌더링한다. 하지만 @RestController 는 반환 값으로 뷰를 찾는게 아니라 HTTP 메시지 바디에 직접 입력한다. 이건 @ResponseBody 와도 연관이 있다. 

***

## 로깅

운영 시스템에서는 `System.out.println()` 과 같은 시스템 콘솔을 사용해서 필요한 정보를 출력하지 않고 별도의 로깅을 통해서 로그를 출력한다. (로그를 사용하면 나중에 찾기도 더 쉽고 클래스 정보나 쓰레드 정보같은 부가 정보를 볼 수도 있고 출력 여부나 모양을 결정할 수 있기 때문에 더 많은 이점이 있다. 그리고 파일로 남기거나 외부 네트워크를 통해 전송할 수도 있다. 성능도 일반 System.out 보다 더 좋다. ) 로깅 라이브러리는 깊이 들어가면 끝이 없기 때문에 비교적 기본적인 것만 여기서 본다. 

로깅 라이브러리는 spring-boot-starter 에 기본적으로 제공해주는 것이 있다. (Spring-boot-startert-logging)

로그 라이브러리는 Logback, Log4j, Log4J2 등 수 많은 라이브러리가 있는데 그것들에 대한 인터페이스를 제공하는게 SLF4J 라이브러리다. Logback은 SLF4J의 구현체라고 생각하면 되고 스프링 부트에서 기본적으로 제공해주는 로그 라이브러리는 Logback 이다. 

기본적으로 보이는 로그는 info,warn,error 만 보인다. 기본적으로 info 레벨까지만 보인다. debug와 trace는 보이지 않는다. 이를 다 보고싶다면 `application.properties` 에 로그를 어느 수준까지 자세하게 볼건지 패키지를 설정하면 된다.  `logging.level.com.example.demo.basic=trace`  운영 서버에는 info로 개발 환경에서는 debug나 trace로 설정하면 된다. (왜냐하면 로그가 너무 많아서. 중요한 정보만 남기기 위해서)

로그의 기본 레벨을 설정하고 싶다면 `logging.level.root` 를 통해 설정할 수 있다. 이를 통해 로그 레벨을 줄이는 건 추천하지 않는다. 너무 많이 출력되기 떄문에. 

롬북이 제공하는 @Slf4j 에노테이션을 통해서 바로 log 객체를 사용할 수 있다. 
 로그의 출력은 다음과 같이 해야한다. 

 ```java
  log.trace("trace log={}" , name); // O
  log.trace("trace log="  + name); // X
 ```

왜냐하면 + 가 있는 순간부터 연산을 한다. 즉 CPU와 메모리를 사용하고 있는 것 근데 출력하지도 않을거면 굳이 연산할 필요가 없다. 그냥 피라미터만 넘기는게 훨씬 낫다. 

***

## 요청 매핑 

요청 매핑은 어떤 컨트롤러가 호출되야 하는 지를 결정하는 것이다. 이건 @RequestMapping(url) 에 입력한 url에 따라서 메소드가 실행된다. 이 값은 배열로도 입력을 받을 수 있어서 두개의 값을 넣을 수 있다. 

스프링에서는 /hello-basic 이 요청과 /hello-basic/ 이 요청을 같은 요청으로 매핑해준다. 실제로는 다른 경로지만

@RequestMapping() 에 method 속성으로 HTTP 메소드를 입력하지 않으면 모든 HTTP 메소드에 호출되게 된다. 만약 요청 HTTP 메소드와 @RequestMapping 에 등록한 method 속성이 다르다면 HTTP 405 상태코드 (Method Not Allowed) 를 반환하게 된다. 

매핑의 축약 표현으로 다음과 같이 가능하다.
  - @GetMapping
  - @PostMapping
  - @PutMapping
  - @DeleteMapping
  - @PatchMapping

@PathVariable 을 통해서 url 자체에 값이 들어가 있는 경우에 이 값을 피라미터로 받을 수 있다. 
 - 리소스 식별자의 이름과 피라미터의 이름이 같다면 @PathVariable 의 이름을 생략할 수 있다. 
 - 그리고 @PathVariable 은 다중매핑이 가능하다. 

url 정보 뿐 아니라 요청 매개변수에 따라서 호출될 메소드도 결정할 수 있다. 

특정 헤더 조건에 따라 요청 매핑을 해줄 수도 있다. 

미디어 타입 조건에 따라 요청 매핑을 할 수도 있다. (Content-Type 에 따라 요청 매핑을 해줄 수 있는 것)

HTTP 헤더의 Accept 타입에 따라 요청 매핑을 할 수 있다. 
  - Produces = "text/html"
  - produces = "!text/html"
  - produces = "text/*"
  - produces = "*\/"
  - 406 번 에러는 미디어 타입이 안맞을 때 발생할 수 있는 상태에러다. 

예시는 다음과 같다.

```java
    @RequestMapping(value = "/hello-basic", method = RequestMethod.GET)
    public String helloBasic(){
        log.info("helloBasic");
        return "helloBasic";
    }

    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data){
        log.info("mappingPath userId={}", data);
        return "ok";
    }

//    @GetMapping("/mapping/{userId}")
//    public String mappingPath2(@PathVariable String userId){
//        log.info("mappingPath2 userId={}", userId);
//        return "ok";
//    }

    @GetMapping("/mapping/{userId}/orders/{orderId}")
    public String mappingPath3(@PathVariable String userId, @PathVariable String orderId){
        log.info("mappingPath3 userId={}, orderId={}", userId, orderId);
        return "ok";
    }

    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingPath4(){
        log.info("mappingPath4");
        return "ok";
    }

    @GetMapping(value = "/mapping-param", params = "mode")
    public String mappingPath5(String mode){
        log.info("mappingPath5 mode={}",mode);
        return "ok";
    }

    @GetMapping(value = "/mapping-param", params = "!mode")
    public String mappingPath6(){
        log.info("mappingPath6");
        return "ok";
    }

    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader(){
        log.info("mapping Header");
        return "ok";
    }

    @PostMapping(value = "/mapping-consume", consumes = "application/json")
    public String mappingConsume(){
        log.info("mapping Consume");
        return "ok";
    }

    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduce(){
        log.info("mappingProduces");
        return "ok";
    }
```

*** 

## 요청 매핑 API 예시 

- 회원 목록 조회: GET `/users` 
- 회원 등록: POST `/users` 
- 회원 조회: GET `/users/{userId}`
- 회원 수정: PATCH `/users/{userId}` 
- 회원 삭제: DELETE `/users/{userId}`
- 이렇게 만들고 Controller에서 @RequestMapping 을 통해 리소스 접근에 계층을 나누면 편하다. 

```java
@RestController
@RequestMapping("/mapping/users")
public class MappingClassController {

    /*
    - 회원 목록 조회: GET `/users`
    - 회원 등록: POST `/users`
    - 회원 조회: GET `/users/{userId}`
    - 회원 수정: PATCH `/users/{userId}`
    - 회원 삭제: DELETE `/users/{userId}`
     */

    @GetMapping
    public String user(){
        return "get Users";
    }

    @PostMapping
    public String addUser(){
        return "post User";
    }

    @GetMapping("/{userId}")
    public String findUser(@PathVariable String userId){
        return "get UserId=" + userId;
    }

    @PatchMapping("/{userId}")
    public String updateUser(@PathVariable String userId){
        return "update User=" + userId;
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId){
        return "delete User=" + userId;
    }
}
```



## HTTP 요청 기본 헤더 조회 

HTTP 요청에서 헤더를 조회하는 방법

```java
@RestController
public class RequestHeaderController {

    @RequestMapping("/headers")
    public String headers(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpMethod httpMethod,
                          Locale locale,
                          @RequestHeader MultiValueMap<String, String> headerMap,
                          @RequestHeader(value = "host", defaultValue = "test") String host,
                          @CookieValue(value = "myCookie", required = false) String cookie){
        log.info("request={}", request);
        log.info("response={}", response);
        log.info("httpMethod={}", httpMethod);
        log.info("locale={}", locale);
        log.info("headerMap={}", headerMap);
        log.info("header Content-Type={}", headerMap.get("User-Agent"));
        log.info("header host={}", host);
        log.info("myCookie={}", cookie);
        return "ok";
    }
}
```
- `MultiValueMap` 은 Map과 유사하지만 하나의 키에 여러 값을 받을 수 있다. HTTP header와 HTTP 쿼리 피라미터와 같이 하나의 키에 여러 값을 받을 때 사용한다. 

  - `keyA=value1&keyA=value2` 가 쿼리 피라미티로 전달됐다고 했을 때 `List<String> values = map.get("keyA") ` 가 될 수 있다. 

- Controller에서 여러가지 애노테이션으로 값을 받을 수 있는데 여기에 속성으로 defaultValue="test" 를 통해 값이 없을 때 가져올 기본 값을 설정할 수 있다.

- 메소드 아규먼트로 받을 수 있는 값과 핸들러에서 리턴할 수 있는 값은 여기서 확인할 수 있다. https://docs.spring.io/spring-framework/docs/current/reference/html/web.html

***

## HTTP 요청 피라미터 




