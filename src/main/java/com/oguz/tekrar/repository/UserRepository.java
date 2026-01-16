package com.oguz.tekrar.repository;

import com.oguz.tekrar.dto.UserNameResponse;
import com.oguz.tekrar.dto.UserSearchDto;
import com.oguz.tekrar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // custom findAll metodu
    @Query("SELECT u FROM User u")
    List<User> tumKullanicilariGetir();

    //kullancıların sadece isimlerini getir
    // constructor expression yöntemi kullanılmıştır.
    @Query("SELECT new com.oguz.tekrar.dto.UserNameResponse(u.name) FROM User u")
    List<UserNameResponse> findUserNames();

    // burada da örneğin "ah" yaz yazdığımız zaman içinde "ah" ile başlayan kullanıcıları getirir
    // ama prefixin başına % eklersek burada içinde "ah" geçenleri getirir önr: Ahmet, Bahar vs.
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT(:prefix, '%'))")
    List<User> searchByPrefix(@Param("prefix") String prefix);

    // Hem ismin başı tutacak HEM DE yaşı tam eşleşecek
    // constructor expression yöntemi kullanılmıştır.
    @Query("SELECT new com.oguz.tekrar.dto.UserSearchDto(u.id,u.name,u.age) FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT(:prefix, '%')) AND u.age = :age")
    List<UserSearchDto> searchByNameAndAge(@Param("prefix") String prefix, @Param("age") Integer age);


    // artan sıralama için ASC kullanılır
    @Query("SELECT u FROM User u ORDER BY u.maas DESC")
    List<User> maasaGoreAzalanSiraylaGetir();

    //gelen bir listenin içindeki elemalarla arama yapma
    @Query("SELECT u FROM User u WHERE u.name IN :isimler")
    List<User> findUsers(List<String> isimler);

    @Query("SELECT COUNT(u) FROM User u WHERE u.age > :age")
    Long countUsers(Integer age);

    @Query("SELECT u FROM User u WHERE u.maas BETWEEN :minMaas AND :maxMaas")
    List<User> findMaasAraligi(Double minMaas, Double maxMaas);

    // adı boş olan kullanıcıları getirme
    @Query("SELECT u FROM User u WHERE u.name IS NULL")
    List<User> findUserByNameIsNull();

    //tüm kullanıcıların maaşları toplamı
    @Query("SELECT SUM(u.maas) FROM User u")
    Double getMaasSum();

}
