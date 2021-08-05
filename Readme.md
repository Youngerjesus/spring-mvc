# Spring Mvc 

https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc
   
***

## 목차
- [Spring MVC 전체 구조](#Spring-MVC-전체-구조)

- [핸들러 매핑과 핸들러 어댑터](#핸들러-매핑과-핸들러-어댑터)

- [뷰 리졸버](#뷰-리졸버)

- [프론트 컨트롤러 패턴](#프론트-컨트롤러-패턴)

- [Jar vs War](#Jar-vs-War)

- [Lombok 설정](#Lombok-설정)

- [@RestController vs @Controller](#@RestController-vs-@Controller)

- [로깅](#로깅)

- [요청 매핑](#요청-매핑)

- [요청 매핑 API 예시](#요청-매핑-API-예시)

- [HTTP 요청 기본 헤더 조회](#HTTP-요청-기본-헤더-조회) 

- [HTTP 요청 피라미터](#HTTP-요청-피라미터)

- [HTTP 요청 파라미터 RequestParam](#HTTP-요청-파라미터-RequestParam)

- [HTTP 요청 파라미터 ModelAttribute](#HTTP-요청-파라미터-ModelAttribute)

- [HTTP 요청 메시지 단순 텍스트](#HTTP-요청-메시지-단순-텍스트)

- [HTTP 요청 메시지 JSON](#HTTP-요청-메시지-JSON)

- [HTTP 응답 정적 리소스 뷰 템플릿](#HTTP-응답-정적-리소스-뷰-템플릿)

- [HTTP 응답 메시지 바디에 직접 입력](#HTTP-응답-메시지-바디에-직접-입력)

- [HTTP 메시지 컨버터](#HTTP-메시지-컨버터) 

- [요청 매핑 핸들러 어댑터 구조](#요청-매핑-핸들러-어댑터-구조)

- [Multipart Resolver](#Multipart-Resolver)

- [예외 처리 핸들러](#예외-처리-핸들러) 

- [@InitBinder](#@InitBinder)

- [서블릿 필터](#서블릿-필터)

- [스프링 인터셉터](#스프링-인터셉터) 

- [Bean Validation](#Validation)

- [타입 컨버터](#타입-컨버터) 

***

## Spring MVC 전체 구조

spring-mvc 에서는 프론트 컨트롤러 패턴을 구현한 DispatcherServlet 이 있다. 이런 DispathcerServlet 도 상속관계를 타고 들어가면 부모로 HttpServlet 이 있다. 

스프링부트는 DispatcherSevlet 을 서블릿으로 자동으로 등록하면서 모든 경로(urlPattern = "/")에 대해서 매핑한다. 

요청 흐름은 다은과 같다. 
  - HttpServlet 은 요청을 받으면 service() 메소드를 호출한다. 이걸 DispatcherServlet 의 부모 클래스인 FrameworkServlet 에서 sevice() 메소드를 오버라이딩 해서 정의했고 이 안에서 DispatcherServlet이 요청을 처리할 알맞은 핸들러를 선택하고 호출하도록 하는 doDispatch() 메소드를 호출한다. 
  - doDispatch() 메소드를 보면 핸들러 매핑 전략을 이용한 getHandler() 메소드를 통해서 요청을 처리할 적합한 핸들러를 찾는다 (컨트롤러를 찾는 걸 말하며 찾지 못하면 404 에러를 보내도록 설정되어있다.) 
  - 그 후 실제 핸들러를 요청할 핸들러어뎁터를 찾기 위해서 getHandlerAdapter() 라는 메소드를 호출한다. (어댑터를 이용하는 이유는 요청을 처리할 오브젝트가 어떠한 것이라도 상관 없도록 하기위해 이를 연결해줄 수 있는 존재가 필요하기 떄문에) 
  - 그 후 HandlerAdpater의 handle() 메소드를 통해 실제 요청할 핸들러를 호출해서 처리하도록 한다. (인터셉트 기능은 빼고 설명한 것) 이떄 뷰를 주는 요청이였다면 ModelAndView 가 리턴이 되고 processDisatchResult() 메소드 안에서 render() 메소드를 통해 `ViewResolver` 가 렌더링 역할을 하는 뷰 객체를 반환해주고 이 뷰를 통해서 렌더링한다. 

주요 인터페이스 목록은 다음과 같다.
  - 핸들러 매핑: `org.springframework.web.servlet.HandleMappiong`
  - 핸들러 어뎁터: `org.springframework.web.servlet.HandlerAdapter`
  - 뷰 리졸버: `org.springframework.web.servlet.ViewResolver`
  - 뷰: `org.springframework.web.servlet.View`

##### doDispatch()

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        boolean multipartRequestParsed = false;
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

        try {
            try {
                ModelAndView mv = null;
                Object dispatchException = null;

                try {
                    processedRequest = this.checkMultipart(request);
                    multipartRequestParsed = processedRequest != request;
                    mappedHandler = this.getHandler(processedRequest);
                    if (mappedHandler == null) {
                        this.noHandlerFound(processedRequest, response);
                        return;
                    }

                    HandlerAdapter ha = this.getHandlerAdapter(mappedHandler.getHandler());
                    String method = request.getMethod();
                    boolean isGet = "GET".equals(method);
                    if (isGet || "HEAD".equals(method)) {
                        long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
                        if ((new ServletWebRequest(request, response)).checkNotModified(lastModified) && isGet) {
                            return;
                        }
                    }

                    if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                        return;
                    }

                    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        return;
                    }

                    this.applyDefaultViewName(processedRequest, mv);
                    mappedHandler.applyPostHandle(processedRequest, response, mv);
                } catch (Exception var20) {
                    dispatchException = var20;
                } catch (Throwable var21) {
                    dispatchException = new NestedServletException("Handler dispatch failed", var21);
                }

                this.processDispatchResult(processedRequest, response, mappedHandler, mv, (Exception)dispatchException);
            } catch (Exception var22) {
                this.triggerAfterCompletion(processedRequest, response, mappedHandler, var22);
            } catch (Throwable var23) {
                this.triggerAfterCompletion(processedRequest, response, mappedHandler, new NestedServletException("Handler processing failed", var23));
            }

        } finally {
            if (asyncManager.isConcurrentHandlingStarted()) {
                if (mappedHandler != null) {
                    mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                }
            } else if (multipartRequestParsed) {
                this.cleanupMultipart(processedRequest);
            }

        }
    }
```



##### getHandler()

```java
@Nullable
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            Iterator var2 = this.handlerMappings.iterator();

            while(var2.hasNext()) {
                HandlerMapping mapping = (HandlerMapping)var2.next();
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }

        return null;
    }
```



##### getHandlerAdapter()

```java
protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            Iterator var2 = this.handlerAdapters.iterator();

            while(var2.hasNext()) {
                HandlerAdapter adapter = (HandlerAdapter)var2.next();
                if (adapter.supports(handler)) {
                    return adapter;
                }
            }
        }

        throw new ServletException("No adapter for handler [" + handler + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }
```



##### handlerAdapter.handle()

```java
// AbstractHandlerMethodAdapter.class
@Nullable
    public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.handleInternal(request, response, (HandlerMethod)handler);
    }


// RequestMappingHandlerAdapter.class
protected ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        this.checkRequest(request);
        ModelAndView mav;
        if (this.synchronizeOnSession) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object mutex = WebUtils.getSessionMutex(session);
                synchronized(mutex) {
                    mav = this.invokeHandlerMethod(request, response, handlerMethod);
                }
            } else {
                mav = this.invokeHandlerMethod(request, response, handlerMethod);
            }
        } else {
            mav = this.invokeHandlerMethod(request, response, handlerMethod);
        }

        if (!response.containsHeader("Cache-Control")) {
            if (this.getSessionAttributesHandler(handlerMethod).hasSessionAttributes()) {
                this.applyCacheSeconds(response, this.cacheSecondsForSessionAttributeHandlers);
            } else {
                this.prepareResponse(response);
            }
        }

        return mav;
    }
```

- `invokeHandlerMethod()` 내부에서 `ArgumentResolver` 가 핸들러에서 처리할 적절한 객체를 생성해준다. 



##### processDispatchResult()

```java
private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv, @Nullable Exception exception) throws Exception {
        boolean errorView = false;
        if (exception != null) {
            if (exception instanceof ModelAndViewDefiningException) {
                this.logger.debug("ModelAndViewDefiningException encountered", exception);
                mv = ((ModelAndViewDefiningException)exception).getModelAndView();
            } else {
                Object handler = mappedHandler != null ? mappedHandler.getHandler() : null;
                mv = this.processHandlerException(request, response, handler, exception);
                errorView = mv != null;
            }
        }

        if (mv != null && !mv.wasCleared()) {
            this.render(mv, request, response);
            if (errorView) {
                WebUtils.clearErrorRequestAttributes(request);
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace("No view rendering, null ModelAndView returned.");
        }

        if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
            if (mappedHandler != null) {
                mappedHandler.triggerAfterCompletion(request, response, (Exception)null);
            }

        }
    }
```



##### render()

```java
protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Locale locale = this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale();
        response.setLocale(locale);
        String viewName = mv.getViewName();
        View view;
        if (viewName != null) {
            view = this.resolveViewName(viewName, mv.getModelInternal(), locale, request);
            if (view == null) {
                throw new ServletException("Could not resolve view with name '" + mv.getViewName() + "' in servlet with name '" + this.getServletName() + "'");
            }
        } else {
            view = mv.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a View object in servlet with name '" + this.getServletName() + "'");
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Rendering view [" + view + "] ");
        }

        try {
            if (mv.getStatus() != null) {
                response.setStatus(mv.getStatus().value());
            }

            view.render(mv.getModelInternal(), request, response);
        } catch (Exception var8) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Error rendering view [" + view + "]", var8);
            }

            throw var8;
        }
    }
```

***

## 핸들러 매핑과 핸들러 어댑터

핸들러 매핑과 핸들러 어댑터가 어떤 것들이 있는지 알아보자. 지금은 사용하지 않지만 과거에 사용했던 것들도 조사해보자.

핸들러 매핑 중 하나는 스프링 빈의 이름으로 핸들러를 찾을 수 있도록 한다. 
  - 예) 요청 url이 `/springmvc/old-controller` 라고 하면 스프링 빈의 이름 중에 `/springmvc/old-controller` 가 있는지 찾아본다. 

핸들러 어댑터 중 하나는 `Controller` 인터페이스를 구현하고 있는 오브젝트를 실행시킬 수 있는 것이 있다. 

스프링 부트를 쓰면 자동으로 등록해주는 핸들러 매핑과 핸들러 어댑터가 있다.
  - 핸들러 매핑
    - 0 = `RequestMappingHandlerMapping` : 에노테이션 기반의 컨트롤러인 @RequestMapping이 붙은 컨트롤러에서 url과 HTTP.METHOD 를 기반으로 핸들러를 찾아준다.
    - 1 = `BeanNameUrlHandlerMapping` : 스프링 빈의 이름으로 핸들러를 찾아준다. 이때 스프링 빈의 이름과 url의 이름이 같아야 한다. 
  - 핸들러 어댑터
    - 0  = `RequestMappingHandlerAdapter` 에노테이션 기반의 컨트롤러인 @RequestMapping이 붙은 컨트롤러를 호출해주는 역할을 한다. 
    - 1 = `HttpRequestHandlerAdapter` HttpReqeustHandler 라는 인터페이스를 구현하고 있는 컨트롤러를 호출해주는 역할을 한다. 
    - 2 = `SimpleControllerHandlerAdapter` Controller 인터페이스를 구현하고 있는 컨트롤러를 호출해준다. 

실행 과정을 보면 다음과 같다. 
  - DispathcherServlet 에서 getHandler() 메소드를 호출하면 핸들러 매핑을 기반으로 핸들러를 찾는다. 
  - 그 후 DispatcherServlet 에서 getHandlerAdpater() 메소드를 호출해서 이 핸들러를 호출해줄 수 있는 핸들러 어댑터를 찾는다. 여기서 좀 더 자세하게 내려가면 HandlerAdatper의 `supports()` 메소드를 통해서 이 핸들러를 호출할 수 있는 어댑터가 맞는지 검사한다. 
  - 그 후 DispatcherServlet 에서 핸들러 어댑터를 찾았다면 핸들러 어댑터를 handle() 메소드를 통해 핸들러를 호출하게 되고 결과를 가져오게 된다. 
  - 이 프로젝트에 있는 `OldController` 예제를 통해서 보면 다음과 같다. 
    - `HandlerMapping` : `BeanNameUrlHandlerMapping`
    - `HandlerAdapter` : `SimpleControllerHandlerAdapter` 

***

## 뷰 리졸버 

뷰 리졸버가 찾을 뷰의 경로를 조금 수정하고 싶다면 `application.properties` 에서 수정하면 된다. 

  - `spring.mvc.view.prefix=/WEB-INF/views` 뷰 파일을 클래스 패스의 `/WEB-iNF/views` 에 두고 싶다면 이렇게 하면 된다.
  - `spring.mvc.view.suffix=.jsp` 를 통해서 html 파일 확장자가 아닌 다른 확장자를 쓸려면 이렇게 하면 된다. 
  - 이렇게 설정해주면 스프링 부트가 등록할 뷰 리졸버인 `InternalResourceViewResolver` 에 설정을 해준다. 

스프링 부트가 자동 등록해주는 뷰 리졸버를 일부만 보자

  - 1 = `BeanNameViewResolver` : 빈 이름으로 뷰를 찾아서 반환해준다. 
  - 2 = `InternalResourceViewResolver` : `spring.mvc.view.suiffix` 나 `spring.mvc.view.prefix` 에 설정한 정보를 바탕으로 뷰를 반환해준다. `InternalResourceViewResolver` 는 내부에서 뷰를 찾을 수 있는 뷰 리졸버이다. 

처리 과정을 보면 다음과 같다.

  - 컨트롤러에서 ModelAndView 를 리턴해주는데 여기에 뷰의 이름도 같이 있다. 
  - DispatcherServlet 의 render() 메소드에서 이 뷰 이름으로 뷰를 찾을 수 있는 뷰 리졸버를 찾는다. 
  - 찾은 뷰 리졸버를 바탕으로 뷰 객체를 생성하고 렌더링한다. 

  





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

***


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

클라이언트에서 서버로 요청 데이터를 보내는 방법은 3가지 방법이 있다.
  - GET - 쿼리 파라미터 
    - 메시지 바디 없이 URL의 쿼리 파라미터에 데이터를 포함해서 전달하는 역할 
    - ex) ?username=hello&age=30
  - POST - HTML Form
    - 메시지 바디에 쿼리 피라미터 형식으로 전달하는 것으로 
    - 메시지 헤더에 보내는 정보는 다음과 같다. Content-Type: application/x-www-form-urlencoded  
  - HTTP message body 
    - HTTP API에서 주로 사용하는 방식으로 Request Body에 직접 데이터를 담아서 요청을 보내는 것
    - 데이터 형식은 주로 JSON을 사용한다. 
    - POST,PUT,PATCH 

***

## HTTP 요청 파라미터 @RequestParam 

스프링에서 제공하는 @RequestParam 을 이용하면 서블릿 방식보다 좀 더 쉽게 사용할 수 있다. 

@RequestParam 이 없어도 스프링에서 받을 수 있지만 이걸 생략하는 건 너무 과할 수 있다. 물론 팀에서 합의가 된다면 뺴는 것도 좋다. 

요청 파라미터에서 필수 파리미터를 추가하는 것도 가능하다 이는 @RequestParam(required = true) 를 통해 가능하다. 물론 기본 값은 true다. 

요청 파라미터에서 기본 값을 설정할 수 있다. 이는 필수 파라미터를 추가하는 것과 연계해서 사용하는 것도 가능하다. @RequestParam(required=true, defaultValue= "guest") 이런 식으로 사용할 수 있다. defaultValue 를 사용하게 되면 required 속성은 필요가 없다. 이게 있든 없든 핸들러에서 사용할 수 있기 때문에.

요청 파리미터를 Map으로 조회하는 것도 가능하다. 그리고 MultiValueMap으로 조회하는 것도 가능하다.  

***

## HTTP 요청 파라미터 @ModelAttribute

실제 개발을 하다보면 필요한건 결국에 객체다. 요청 파라미터를 기반으로 객체를 만들어줘야 한다. 스프링에서는 이를 보다 쉽게 할 수 있도록 지원해준다 이게 바로 @ModelAttribute 이다. 그리고 @ModelAttribute 도 생략이 가능하다. 

@RequestParam 도 생략이 가능하고 @ModelAttribute 도 생략이 가능해서 혼란이 올 수 있겠지만 단순 Primitive 타입인 경우에는 @RequestParam 을 사용하고 나머지인 경우에는 @ModelAttribute 를 사용한다. (Argument resolve로 지정해둔 타입 외) 

@ModelAttribute 의 name 속성으로 뷰에서 사용할 객체 이름을 지정할 수 있다. 

이 과정을 어떻게 하는지 알아보면 다음과 같다. 
  1. 스프링에서는 먼저 @ModelAttribute 로 선언한 객체를 생성한다. 
  
  2. 그 후 요청 파라미터의 이름으로 이 객체의 프로퍼티를 찾고 해당 프로퍼티의 setter 메소드를 호출해서 넣어준다. (바인딩)

- 오로지 setter 로만 바인딩을 하는지 궁금해서 모든 파라미터를 받는 생성자가 있고 setter 메소드가 없는 경우에 처리를 해보니 잘 된다. 하지만 기본 생성자가 있으면 값이 들어가지 않는다. 

- 그리고 요청 파리미터가 잘못 입력되서 `BindException` 이 생길 수 있다. 이를 Validation 에서 하도록 관리해야 한다. 

또 공통적으로 참고해야 하는 모델 정보가 있다면 컨트롤러에서 @ModelAttribute 메소드를 만들어서 정의할 수 있다. 

***

## HTTP 요청 메시지 단순 텍스트 

HTTP 에서 클라이언트 -> 서버로 데이터를 보내는 방법 중 HTTP message body 에 데이터를 직접 담아서 요청하는 경우가 있다. 주로 HTTP API 에서 주로 사용하고 요청을 보내는 타입은 JSON 을 주로 이용한다. 

요청 파라미터와 다르게 Request Body에 데이터가 담겨서 들어오는 경우에는 @RequestParam 이나 @ModelAttribute 를 사용할 수 없다. (물론 HTTP Form 요청인 경우에는 이게 가능하지만.) 

스프링 mvc 에서는 HttpServletRequest 나 HttpServletResponse 에 대한 객체도 핸들러에서 받을 수 있지만 InputStream, OutputStream, Writer 에 대한 객체도 받을 수 있다. 

또 스프링 mvc에서 지원하는 객체는 HttpEntity 로 HTTP Request 의 Header 나 Body 를 직접 조회할 수 있다. HttpEntity는 요청 파라미터와는 관련이 없다. 그리고 이를 응답에도 사용이 가능하다. view 조회는 아니지만. 
  - HttpEntity 를 상속받은 RequestEntity 나 ResponseEntity 를 사용하는 것도 가능하다. 
  - ResponseEntity 같은 경우에는 상태코드를 넣는 것이 가능하다. 
  - RequestEntity 같은 경우에는 url 정보나 Http Method 정보를 추가로 사용 가능하다. 
  - 참고로 알면 좋은 사실은 스프링 MVC 내부에서 HTTP 메시지 바디을 읽어서 변환해주는 역할을 HttpMessageConverter 이 해준다. 

이보다 더 쉬운 방법으로 스프링에서는 @RequestBody 와 @ResponseBody 를 제공해준다. 이 에노테이션을 붙으면 HTTP Request Body 에 있는 내용을 가지고 와주거나 HTTP Response Body 에 직접 값을 써주도록 할 수 있다. 

***

## HTTP 요청 메시지 JSON 

@RequestBody 를 이용해서HTTP Request Body에 있는 정보를 바탕으로 직접 만든 객체로 사용하는게 가능하다. 이게 가능한 이유가 HttpMessageConverter 가 HTTP Request Header 에 있는 Content-Type 정보를 보고 판단해서 변환해준다. 이 HttpMessageConverter 는 텍스트 뿐 아니라 JSON 타입도 알아서 변환해준다. 

@RequestBody 는 @RequestParam 이나 @ModelAttribute 와는 다르게 생략하면 안된다. 왜냐하면 스프링 mvc는 원시 타입인 경우에 @RequestParam 으로 객체 타입인 경우에는 @ModelAttribute 로 적용하기 떄문에. 

만약에 해더에 대한 정보나 url에 대한 정보, HTTP Method 에 대한 정보가 추가로 필요하다면 RequestEntity<> 타입으로 받을 수도 있다. 

@ResponseBody 에노테이션이 붙어있다면 return 에 객체를 하면 그 값 그대로 HTTP Response Body에 쓰인다. 이 역할도 HttpMessageConverter에 의해 이뤄진다. 


***

## HTTP 응답 정적 리소스 뷰 템플릿

스프링에서 응답 데이터를 만드는 방법은 크게 3가지이다. 
  - 정적 리소스
    - 웹 브라우저에서 정적인 HTML,css, js 를 제공할 땐 정적 리소스를 사용한다.
    - 스프링 부트는 클래스패스의 다음 디렉토리에 있는 정적 리소스를 제공한다
      - `/static`
      - `/public`
      - `/resources`
      - `/META-INF/resources`
      - `src/main/resources` 는 리소스를 보관하는 곳이고 또 클래스패스의 시작 경로이다. 따라서 다음 디렉토리에 리소스를 넣어두면 스프링 부트가 정적 리소스로 서비스를 제공한다. `src/main/resources/static`
        - 예로 `src/main/resources/static/basic/hello-form.html` 파일이 존재한다면 웹 브라우저에서 다음과 같이 이용이 가능하다. `http://localhost:8080/basic/hello-form.html` 
  - 뷰 템플릿 사용
    - 웹 브라우저에서 동적인 HTML을 제공할 땐 뷰 템플릿을 사용한다. 
    - HTML 뿐만 아니라 뷰 템플릿이 만들 수 있는 것이라면 뭐든 가능하다. 
    - 뷰 템플릿 경로는 다음과 같다. `src/main/resources/templates` 
    - 컨트롤러에서 String을 반환하는 경우에 `ResponseBody` 가 없다면 뷰 리졸버가 실행되어서 뷰를 찾고 렌더링 한다. `ResponseBody` 가 있다면 HTTP 메시지 바디에 직접 데이터를 넣고. 
    - 컨트롤러에서 void를 반환하는 경우에 요청 URL을 참고해서 뷰 이름으로 사용한다. 이 방식은 명시성이 너무 떨어져서 사용하지는 않는다. 
HTTP 메시지 사용
  - HTTP API를 제공하는 경우에는 HTML이 아니라 데이터를 전달해야 하므로 HTTP 메시지 바디에 JSON 같은 형식으로 데이터를 실어 보낸다. 

***

## HTTP 응답 메시지 바디에 직접 입력 

HTML이나 뷰 템플릿을 사용해도 HTTP 응답 메시지 바디에 HTML 데이터가 담겨서 전달된다. 여기서 말할 것은 정적 리소스나 뷰 템플릿을 거치지 않고 직접 HTTP 응답 메시지를 입력하는 방식이다. 

ResponseEntity 를 사용하지 않더라도 @ResponseStatus 를 통해 상태 코드를 넣을 수 있다. 하지만 이는 동적으로 상태 코드를 넣는건 힘들다. 

@ResponseBody 를 메소드마다 붙이는게 중복이라면 클래스 단에서 @RestController를 이용하면 된다. 

***

## HTTP 메시지 컨버터

뷰 템플릿으로 HTML을 생성해서 응답하는 것이 아니라 HTTP API 처럼 JSON 데이터를 HTTP 메시지 바디에서 직접 읽거나 쓰는 경우 HTTP 메시지 컨버터를 이용하면 편하다. 

@ResponseBody를 이용하면 HTTP 요청 바디에 직접 데이터를 쓰게 된다. 이게 어떻게 동작하는지 살펴보면 다음과 같다. 

  1. @ResponseBody 를 보고 `viewResolver` 대신에 `HttpMessageConverter` 가 동작한다..
  
  2. 기본 문자처리는 `StringMessageConverter` 가 돌아가고 기본 객체처리는 `MappingJackson2HttpMessageConverter` 가 동작해서 메시지 바디에 넣어준다. 

응답의 경우에는 클라이언트의 HTTP Accept 헤더 정보와 컨트롤러의 반환 타입을 조합해서 HTtpMessageConverter가 선택된다. 

HTTP 메시지 컨버터는 HTTP 요청이나 HTTP 응답 둘 다 사용된다

  - `canRead()` 나 `canWrite()` 메소드를 통해 메시지 컨버터가 해당 클래스(String, 객체, Byte[])나 미디어타입을 지원하는지 체크하고`read()` 나 `write()` 를 통해 메시지를 읽고 쓰는 기능을 지원한다. 

스프링 부트의 기본 메시지 컨버터는 다음과 같다. 

  - `ByteArrayHttpMessageConverter`
    - 요청 데이터를 `byte[]` 로 받는다. 이때 `Content-Type` 가 어떤것이든 상관없다. 
    - 요청 예) `@ReqeustBody byte[] data` 
    - 응답 예) `@ResponseBody return byte[]` 쓰기 미디어타입은 `application/octet-stream`  이다. 
  - `StringHttpMessageConverter`
    - 요청 데이터를 `String` 으로 받는다. 이때 `Content-Type` 은 어떤것이든 상관없다. 
    - 요청 예) `@RequestBody String data` 
    - 응답 예) `@ResponseBody return "ok"` 쓰기 미디어타입은 `text/plain` 이다. 
  - `MappingJackson2HttpMessageConverter` 
    - 요청 데이터를 클래스 타입의 객체 또는 `HashMap` 으로 받는다. 이때 `Content-Type: application/json` 으로 요청을 해야한다.
    - 요청 예) `@RequestBody HelloData data` 
    - 응답 예) `@ResponseBody return helloData` 이고 쓰기 미디어타입은 `application/json` 이다.   
  - 스프링 부트는 다양항 메시지 컨버터를 지원한다. 바이트로 변환할지 스트링으로 변환할지 JSON으로 변환할지 등등.

***

## 요청 매핑 핸들러 어댑터 구조 

HTTP 메시지 컨버터는 스프링 MVC에서 어디쯤에서 사용되는 걸까? 

스프링 MVC에서 처리과정을 보면 `DispatcherServlet` 이 핸들러 어댑터에서 핸들러(컨트롤러)를 호출할 때 쯤에 위치한다고 생각할 수 있다. 즉 모든 비밀은 에노테이션 기반의 컨트롤러, 그러니까 @RequestMapping 을 처리하는 핸들러 어댑터인 `RequestMappingHandlerAdapter` 가 이 일을 해준다. 

`RequestMappingHandlerAdapter ` 처리과정
  - 어떻게 컨트롤러는 다양한 매개변수를  받아서 처리할 수 있을까? (`HttpServletReqeust` `InputStream` `@RequestParam` `@RequestBody` 이런 것들 ) 이 역할을 해줄 수 있는게 `ArgumentResolver`  스프링은 `ArgumentResolver` 를 호출해서 30개가 넘는 파라미터들을 생성해줄 수 있다. 그리고 이런 파라미터들이 모두 준비가되면 컨트롤러에 전달해서 호출해준다. 
  - `ArgumentResolver` 의 실제 이름은 `HandlerMethodArgumentResolver` 이다. 여기서 `supportsParameter()` 라는 메소드를 통해서 이 파라미터를 지원할 수 있는지 알 수 있다. 지원하면 `resolveArgument()` 메소드를 호출해서 실제 객체를 생성한다. 
  - 원한다면 직접 이 인터페이스를 확장해서 `ArgumentResolver` 를 만들고 이를 컨트롤러에서 받을 수 있도록 할 수 있다. 

`ReturnValueHandler` 란 뭘까?
  - 컨트롤러에서 반환하는 종류는 여러개가 있다. 언제는 `ModelAndView` 를 이용해 동적 페이지를 줄 수 있고 String을 반환할 수도 있고 객체를 반환할 수 있다.  물론 `ResponseEntity` 도 가능하고. 
  - 실제 이름은 `HandlerMethodReturnValueHandler` 이다. `ReturnValueHandler` 에서 컨트롤러의 응답을 보고 실제로 사용자에게 보낼 응답 메시지를 만들어준다. 

그렇다면 이제 HTTP 메시지 컨버터는 어떤 일을 할까? 
  - `@RequestBody` 를 보고 적절한 객체로 반환해주는게 `ArgumentResolver` 이다. 이 안에서 HTTP 메시지 컨버터가 작동을 하는 것. `@ResponseBody` 도 마찬가지. 여기서 HTTP 메시지 컨버터 를 이용해서 필요한 객체를 생성하는 것이다. 
  - 실제 예로 `HttpEntityMethodProcessor` 를 보면 (이 객체도 `HandlerMethhodArgumentResolver` 를 부모로 가지고 있다.)  `supportParameter()` 메소드를 통해 이 파라미터를 생성할 수 있는지 체크를 하고. `supportReturnValue()` 메소드를 통해 이 응답 객체를 생성할 수 있는지 체크를 한다. 그 후 요청의 경우 `resolveArgument()` 라는 메소드 안에서 `readWithMessageConverters()` 메소드를 통해 메시지 컨버터를 이용해 요청 파라미터로 전달할 객체를 생성한다. 
  - 실제 다양한 타입의 객체를 만들어서 컨트롤러에 전달하는게  `ArugmentResolver` 이고 여기 안에서 컨트롤러의 파라미터 정보를 읽고 클라이언트가 보낸 데이터 타입을 바탕으로 다양한 객체로 변환해주는게 메시지 컨버터이다. (`canRead()` `read()` `canWrite()` `write()` ) 

***

## Multipart Resolver
   
파일업로드시 사용하는 메소드 아규먼트로 이를 사용할라면 MultipartResolver 빈이 설정 되어 있어야 사용할 수 있다. 

스프링 부트에서는 기본적으로 MultipartResolver 를 빈으로 제공해준다.

POST multipart/form-data 요청에 들어있는 파일을 참조할 수 있다. 

***

## 예외 처리 핸들러 
   
spring-mvc 에서 직접 에러를 만들어서 발생시켰거나 자바에서 지원하는 에러가 발생했을때 우리가 정의한 핸들러로 처리해서 응답을 만들어 내는 것

@ExceptionHandler 를 통해서 사용이 가능하다. 

@ExceptionHandler 에서 받을 수 있는 Method Argument 로는 크게 해당 예외인 `ExceptionType` 과 예외가 발생한 메소드인 `HandlerMethod` 를 받을 수 있다. ModelAttribute 는 지원하지 않는다. 

예외 처리 핸들러에서 여러개의 예외를 받아서 처리할 수 있다. 이때는 이들의 상위클래스로 둬야한다. 
 

***

## @InitBinder

특정 컨트롤러에서 데이터를 바인딩하고 검증할 때 사용하는 것 

##### @InitBinder 기본 설정 
```java
// @InitBinder 에 이름을 입력하면 컨트롤러가 받는 객체중에 특정 객체만 Binding 을 하거나 Validate 를 적용시킬 수 있다.
@InitBinder("Event")
public void initBinder(WebDataBinder webDataBinder){
    // 객체의 프로퍼티 중 id라는 필드가 있는 경우 필터링을 해준다.
    webDataBinder.setDisallowedFields("id");

    // setAllowedFields()는 허용할 필드명을 명시하고 그 값들만 허용해준다. 
    webDataBinder.setAllowedFields("id");

    // 특정 Formatter를 등록해줄 수 있다.  
    webDataBinder.addCustomFormatter();

    // 특정 CustomEditor 등록해줄 수 있다. 
    webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(new ISO8601DateFormat(), false));

    // 특정 Validator를 등록해줄 수 있다. 
    webDataBinder.addValidators(new EventValidator());
}
```
 
##### Validator Example - EventValidator
```java
public class EventValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        // 어떤 도메인에 해당 validate를 지원할 것인지 명시해준다.
        return Event.class.isAssignableFrom(clazz);
        or
        return Event.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Event event = (Event) o;
        if (event.getName() == "goodgid") {
            errors.rejectValue("name", event.getName() + " is wrongValue");
        }
    }
}
```

***

## 서블릿 필터 

서블릿 필터는 공통 관심 사항 처리에 사용할 수 있다.

그렇다면 공통 관심 사항이란 말은 뭘까?

- ex) 로그인을 한 사용자만 관리 페이지에 들어갈 수 있어야 한다.

스프링 AOP 를 사용하면 공통 관심 사항을 처리할 수 있겠지만 HTTP REQUEST 정보로 웹과 관련된 공통 관심사를 처리하고 싶다면 서블릿 필터나 인터셉터를 이용해서 처리하면 된다.

서블릿 필터는 요청을 맞이하는 수문장과 같은 존재다. 필터를 사용하면 적절하지 않은 요청이라고 판단하면 거기서 끝낼 수 있다. 

- HTTP 요청 -> WAS -> 필터 -> 필터 ... -> 마지막 필터 -> 서블릿 -> 컨트롤러 

  - chain.doFilter(request, response) 메소드를 통해서 다음 필터를 계속해서 호출하고 마지막 필터는 서블릿을 호출한다. 
  
필터를 등록하는 방법은 여러개가 있지만 스프링 부트는 내장된 톰캣이 있으므로 FilterRegistrationBean 을 이용해서 등록하면된다.

- setFilter() 메소드를 통해서 등록할 필터를 지정할 수 있다.

- setOrder() 메소드를 통해서 필터 체인의 순서를 등록할 수 있다. 

- addUrlPattern() 을 통해서 필터를 적용할 URL 패턴을 지정할 수 있다. 

실무에서 HTTP 요청시 로그에 모두 같은 식별자를 남기고 싶다면 logback.mdc 를 이용하면 된다. 
  
***

## 스프링 인터셉터 

스프링 인터셉터도 서블릿 필터와 같이 웹과 관련된 공통 관심 사항을 효과적으로 해결할 수 있는 기술이다. 

서블릿 필터는 서블릿이 제공하는 기술이라면, 스프링 인터셉터는 스프링 MVC 가 제공하는 기술이다. 

인터셉터를 포함한 웹의 흐름은 다음과 같다. 

- HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러  

스프링 인터셉터를 이용해서 서블릿 필터와 같은 기능을 제공해줄 수 있다. 하지만 스프링 인터셉터가 더 정교한 기능을 제공해준다. 

- 서블릿 필터의 경우 doFilter() 메소드만 지원을 해주는 반면에 스프링 인터셉터는 컨트롤러 호출 전 메소드인 preHandle(), 컨트롤러 호출 후 메소드인 postHandle(), 요청 완료 이후 메소드인 afterCompletion() 메소드
등 단계적으로 잘 지원해준다. 

- 인터셉터와 서블릿 필터의 순서에 대해서 좀 더 알아보자면 인터셉터의 afterCompletion() 이후에 서블릿 필터를 거쳐서 응답이 나간다.  

  - preHandle() 은 핸들러 어댑터 전에 즉 컨트롤러 호출 전에 호출되고 여기서 true 를 리턴하면 다음으로 진행하지만 false 를 리턴하면 더는 진행하지 않는다.
  
  - postHandle() 은 컨트롤러 호출 후 즉 HandlerAdapter 가 컨트롤러 메소드를 호출 한 다음에 호출된다. 컨트롤러에서 예외가 발생되면 postHandle() 은 호출되지 않는다.
  
  - afterCompletion() 은 요청 완료 이후에 즉 뷰가 렌더링 된 이후에 호출된다. 컨트롤러에서 예외가 발생해도 afterCompletion() 은 호출된다. 어떤 예외가 났는지 알 수도 있다. 
  
***

## Validation 

컨트롤러의 중요한 역할 중 하나는 HTTP 요청이 서버가 원하는 정상적인 요청인지 검증하는 것이다.

주로 검증은 다음과 같다.

- 타입 검증: 가격, 수량에 문자가 들어있으면 검증 오류를 처리하는 것이다.

- 필드 검증: 공백이 있거나 값이 비어있으면 안된다던지, 어떤 수량의 제한 조건이 있는지 말하는 것이다.  

검증은 클라이언트 검증과 서버 검증이 있을 수 있는데 클라이언트 검증만 한다면 조작을 할 수 있으므로 완벽하지 않고 서버만으로 검증을 한다면 즉각적인 피드백을 클라이언트에게 줄 수 없으므로 UX 가 떨어지는 문제가 생길 수 있다.
그러므로 이 둘을 적절히 섞는게 중요하다.  

이러한 검증은 Bean Validation 을 이용하면 에노테이션 기반으로 검증을 할 수 있어서 편하다. 

- Bean Validation 을 위한 의존성 추가로 `spring-boot-starter-validation` 을 추가하면 된다.  
이 의존성을 추가해두면 스프링 부트에서 `LocalValidatorFactoryBean` 이 글로벌 Validator 로 등록되고 그러므로 @Validated 에노테이션을 걸면 검증이 된다. 

검증 에노테이션으로는 다음과 같다. 

- `@NotBlank` : 빈값 + 공백만 있는 경우를 허용하지 않는다.

- `@NotNull` : null 을 허용하지 않는다.

- `@Range(min = 1000, max = 110000` : min ~ max 범위 안의 값이어야 한다. 

- `@Max(9999)` : 최대값이 9999 까지만 포함한다.  

***

## 타입 컨버터 

문자를 숫자로 변환하거나, 반대로 숫자를 문자로 변환해야 하는 경우 처럼 애플리케이션을 개발하다 보면 타입을 변환해야 하는 경우가 많다. 

#### HelloController 에서 문자 타입을 숫자 타입으로 변경 

```java
@RestController
public class HelloController {

    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest request) {
        String data = request.getParameter("data");
        Integer intValue = Integer.valueOf(data);
        System.out.println("intValue = " + intValue);
        return "ok";
    }
}
```

#### 스프링 MVC 가 제공해주는 @RequestParam 을 이용하면 자동으로 타입변환을 해준다. 

```java
@GetMapping("/hello-v2")
public String helloV2(@RequestParam Integer data) {
    System.out.println("data = " + data);
    return "ok";
}
```

- @ModelAttribute 와 @PathVariable 도 타입 변환을 알아서 해준다. 

스프링은 이렇게 알아서 타입 변환을 해주는 경우가 많다.

스프링 MVC 의 요청외에도 프로퍼티의 값을 읽어오는 에노테이션인 @Value 에도 타입 변환이 들어가야하고 XML 에 넣은 스프링 빈 정보가 있을때도 타입 변환을 해야하며
뷰를 렌더링 할때도 타입 변환이 필요하다. 

하지만 만약에 개발자가 새로운 타입을 만들어서 변환하고 싶다면 어떻게 해야할까? 

스프링은 확장 가능한 컨버터 인터페이스를 제공하고 이 컨버터 인터페이스를 구현해서 등록하면 다른 타입 변환에도 사용하는게 가능하다. 

컨버터 인터페이스는 다음과 같이 있다. 

```java
package org.springframework.core.convert.converter;

public interface Converter<S, T> {
      T convert(S source);
}
```

- 이 컨버터 인터페이스는 모든 타입에 적용하는게 가능하다. 

- 예를 들어서 문자로 `true` 가 오면 Boolean 타입으로 받도록 할 수 있고 반대로 적용하고 싶으면 또 다른 컨번터를 만들어서 적용하면 된다. 
컨버터는 단방항이기 떄문이다. 

다음과 같이 컨버터를 만들 수 있다.  

#### 문자 -> 숫자로 변환해주는 컨버터 

```java
@Slf4j
public class StringToIntegerConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String s) {
        log.info("converter source = " + s);
        return Integer.valueOf(s);
    }
}
```

#### 숫자 -> 문자로 변환해주는 컨버터

```java
@Slf4j
public class IntegerToStringConverter implements Converter<Integer, String> {

    @Override
    public String convert(Integer source) {
        log.info("source = " + source);
        return String.valueOf(source);
    }
}
```

#### 사용자 정의 컨버터 문자 -> IpPort 컨버터

```java
@Getter
@EqualsAndHashCode
public class IpPort {
    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
}

@Slf4j
public class StringToIpPortConverter implements Converter<String, IpPort> {

    @Override
    public IpPort convert(String source) {
        log.info("convert source = " + source);
        String[] split = source.split(":");
        String ip = split[0];
        int port = Integer.parseInt(split[1]);
        return new IpPort(ip, port);
    }
}
```
> 참고로 스프링에서는 용도에 따라 다양한 방식의 컨버터를 제공해주기도 한다.
> `Converter` 기본 타입 컨버터: 
> `ConverterFactory` 전체 클래스 계층 구조가 필요로 할 때 사용된다. 
> `GenericConverter` 정교한 구현이나 대상 필드의 에노테이션 정보를 사용 가능한 컨버터다. 
> `ConditionalGenericConverter` 특정 조건이 참인 경우에 컨버터를 실행하는 컨버터다. 
>
> 좀 더 자세한 내용은 공식 문서를 보자. https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core- convert   

이렇게 만든 타입 컨버터를 매번 직접 찾아서 사용하기에는 불편하기 때문에 스프링에서는 컨버터를 모아둬서 그것들이 변환할 수 있다면 변환해주는 그런 서비스를 제공해주는데
이게 `ConversionService` 라고 한다. 

#### ConversionService 인터페이스 

```java
package org.springframework.core.convert;
import org.springframework.lang.Nullable;
        
public interface ConversionService {
    boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);
    boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
    
    <T> T convert(@Nullable Object source, Class<T> targetType);
    
    Object convert(@Nullable Object source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType);
}
``` 

#### WebMvcConfigurer 를 통해서 컨버터를 동록하면 된다. 

```java
@Configuration
public class ConverterConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToIntegerConverter());
        registry.addConverter(new IntegerToStringConverter());
        registry.addConverter(new StringToIpPortConverter());
    }
}
```
