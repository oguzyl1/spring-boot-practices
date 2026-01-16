# Spring Boot Data JPA & Auditing Practices

Bu proje, Spring Boot 3 ile **Data JPA, Entity İlişkileri, Auditing ve DTO/Mapper** yapılarının güncel "best practice"lere uygun olarak uygulanmasını içeren bir backend çalışmasıdır.

Temel CRUD işlemlerinin ötesinde, veritabanı tutarlılığı ve kodun sürdürülebilirliği üzerine odaklanılmıştır.

## 🛠 Kullanılan Teknolojiler ve Kütüphaneler

* **Java 21** & **Spring Boot 3.4.3**
* **Spring Data JPA** (PostgreSQL)
* **Lombok** (Boilerplate kod temizliği için)
* **MapStruct** (Entity <-> DTO dönüşümleri için)
* **OpenAPI / Swagger UI** (API Dokümantasyonu)
* **JPA Auditing** (Otomatik oluşturma/güncelleme tarihleri ve kullanıcı takibi)

## 🏗 Mimari ve Öne Çıkan Özellikler

### 1. Entity İlişkileri
* **One-to-One:** `User` ve `Site` arasında. (Lazy Fetching stratejisi ile performans optimizasyonu)
* **One-to-Many:** `User` ve `Role` arasında. (`orphanRemoval = true` ile tutarlı veri silme yönetimi)

### 2. JPA Auditing
* `BaseEntity` sınıfı ile tüm tablolarda `createdAt`, `updatedAt`, `createdBy`, `updatedBy` alanlarının otomatik yönetimi.
* `AuditorAware` implementasyonu ile işlem yapan kullanıcının ID'sinin otomatik yakalanması.

### 3. Katmanlı Mimari & DTO Pattern
* Entity'lerin doğrudan dışarıya açılması engellendi.
* **MapStruct** kullanılarak performanslı ve temiz veri dönüşümü sağlandı.
* Global `JacksonConfig` ile JSON tarih formatları standartlaştırıldı (`yyyy-MM-dd HH:mm:ss`).

### 4. API Dokümantasyonu
* Swagger UI entegrasyonu yapıldı. Proje ayağa kalktığında `/swagger-ui/index.html` adresinden endpointler test edilebilir.

### 5. Advanced JPQL (Java Persistence Query Language)
Repository katmanında standart JPA metodlarının yetersiz kaldığı durumlar için **`@Query`** anotasyonu ile özel JPQL sorguları yazılmıştır:
* **Projection (DTO Mapping):** Veritabanından tüm entity'yi çekmek yerine, sadece ihtiyaç duyulan alanların çekilip Constructor Expression (`new com.oguz...`) ile doğrudan DTO'ya dönüştürülmesi (Performans artışı).
* **String Manipülasyonları:** `LOWER`, `CONCAT`, `LIKE` fonksiyonları ile büyük/küçük harf duyarsız (Case-insensitive) dinamik aramalar.
* **Aggregate Functions:** `SUM`, `COUNT` gibi fonksiyonlarla veritabanı seviyesinde hesaplamalar.
* **Complex Conditions:** `IN`, `BETWEEN`, `IS NULL` ve `ORDER BY` gibi operatörlerin kullanımı.

## 🧪 Test Stratejileri (Testing)
Projede **Unit Test** ve **Integration (Slice) Test** yaklaşımları hibrit olarak kullanılmıştır:

* **Service Katmanı:** Saf **Unit Test** prensipleriyle, Mockito kullanılarak izole edilmiştir. Veritabanı bağımlılığı olmadan iş mantığı test edilir.
* **Controller Katmanı:** `@WebMvcTest` kullanılarak **Slice Test** uygulanmıştır. Sadece web katmanı ayağa kaldırılarak HTTP istekleri, JSON dönüşümleri ve Exception Handling mekanizmaları `MockMvc` ile test edilmiştir.
* **Repository Katmanı:** Standart JPA metodları için gereksiz test yazılmamış, sadece özel JPQL sorguları (varsa) `@DataJpaTest` ile test kapsamına alınmıştır.

## 🚀 Kurulum

1.  PostgreSQL veritabanında `app_db` adında bir database oluşturun.
2.  `application.yml` dosyasındaki veritabanı kullanıcı adı ve şifresini güncelleyin.
3.  Projeyi çalıştırın.

## 📅 Yol Haritası (Next Steps)

Bu proje yaşayan bir repodur. İlerleyen aşamalarda eklenecek özellikler:

- [x] **JPQL & Custom Queries:** Repository katmanında DTO Projection, Aggregations ve mantıksal operatörler içeren özel sorgular.
- [ ] **Criteria API:** Dinamik sorgu oluşturma örnekleri.
- [ ] **Security:** `AuditorAware` içindeki hardcoded ID'nin Spring Security Context'ten alınması.