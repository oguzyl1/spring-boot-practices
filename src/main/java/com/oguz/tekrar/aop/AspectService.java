package com.oguz.tekrar.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;

@Service
@Aspect
@Slf4j
public class AspectService {

    // @Slf4j de bu işe yarıyor zaten
    // public static Logger log = LoggerFactory.getLogger(AspectService.class);


    // burada com.oguz.tekrar.service paketi altındaki tüm classların tüm metodları çalışmadan önce bu metod çalışacak diyoruz
    // birinci * geriye dönen tip önemsiz, hedef metod ne dönerse dönsün bunu çalıştır demek
    // ikinci ve üçüncü * ise bu paketin içindeki tüm classların tüm metodları için geçerli demek.
    @Before("execution(* com.oguz.tekrar.service.*.*(..))")
    public void before() {
        log.info("Servis sınıfı metodları çalışacak");
    }

    // burada ise metodlar çalıştıktan sonra (başarılı veya başarısız olması önemli değil) işlem gerçekleşecek.
    @After("execution(* com.oguz.tekrar.service.*.*(..))")
    public void after() {
        log.info("Servis sınıfı metodları çalıştı (başarılı veya başarısız)");
    }

    @After("execution(* com.oguz.tekrar.service.*.*(Integer)) || execution(* com.oguz.tekrar.service.*.*(int))")
    public void after2() {
        log.info("Servis sınıfında Integer veya int parametresi alan metodlar çalıştı (başarılı veya başarısız)");
    }

    // burada ise metodun başarılı çalıştığı durumlarda çalışır
    @AfterReturning("execution(* com.oguz.tekrar.service.*.*(..))")
    public void afterReturning() {
        log.info("Servis sınıfı metodu başarıyla çalıştı");
    }

    // burada ise metod eğer hata fırlatırsa çalışacak
    @AfterThrowing("execution(* com.oguz.tekrar.service.*.*(..))")
    public void afterThrowing() {
        log.info("Servis sınıfı metodu hata fırlattı.");
    }

    @AfterReturning(pointcut = "execution(* com.oguz.tekrar.service.*.*(..))", returning = "result")
    public void afterReturning2(Object result) {
        log.info("Servis sınıfı metodu başarıyla çalıştı, dönüş: {}", result);
    }

    // burada joinPoint ile hata fırlatan sınıfın içerisindeki bilgileri çekebiliyoruz
    @AfterThrowing(pointcut = "execution(* com.oguz.tekrar.service.*.*(..))", throwing = "ex")
    public void afterThrowing2(JoinPoint joinPoint, Exception ex) {
        log.info("Servis sınıfı metodu hata fırlattı: {}", ex.getMessage());
        String name = joinPoint.getSignature().getName();
        log.info("metod adı: {}", name);
    }

    @Before("execution(* com.oguz.tekrar.service.*.*(..))")
    public void before1(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            log.info("1 - Metoda gelen parametre: {}", arg); // UserRequest(name=string, surname=string, email=strewqing, password=string, maas=0.1, age=0)
        }

        Object target = joinPoint.getTarget();
        log.info("2- Metod şu sınıfta tanımlı: {}",target); // com.oguz.tekrar.service.UserService@3c403b36

        Object proxy = joinPoint.getThis().getClass().getName();
        log.info("3 - Spring proxy sınıfı: {}",proxy); // com.oguz.tekrar.service.UserService$$SpringCGLIB$$0
    }

    @Around("execution(* com.oguz.tekrar.controller.UserController.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        log.info("around ile metod çalışmadan önce çalışacak kısım");

        var methodName = pjp.getSignature().getName();
        log.info("Around (önce) : {}",methodName);

        var result = pjp.proceed(); // BUNDAN ÖNCESİ METOD ÇALIŞMADAN ÖNCE BUNDAN SONRASI ÇALIŞTIKTAN SONRA

        log.info("around ile Metod Çalıştıktan Sonra çalışacak kısım");
        log.info("Around (sonra) : {}", methodName);

        return result;
    }
}
