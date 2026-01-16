package com.oguz.tekrar.repository;

import com.oguz.tekrar.dto.UserNameResponse;
import com.oguz.tekrar.dto.UserSearchDto;
import com.oguz.tekrar.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user1 = new User();
        user1.setName("Ahmet");
        user1.setAge(25);
        user1.setMaas(50000.0);

        User user2 = new User();
        user2.setName("Mehmet");
        user2.setAge(30);
        user2.setMaas(60000.0);

        User user3 = new User();
        user3.setName("Ayşe");
        user3.setAge(28);
        user3.setMaas(75000.0);

        User user4 = new User();
        user4.setAge(40);
        user4.setMaas(40000.0);

        userRepository.saveAll(Arrays.asList(user1, user2, user3, user4));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("JPQL: Tüm kullanıcıları getirmeli")
    void tumKullanicilariGetir_ShouldReturnAllUsers() {
        List<User> users = userRepository.tumKullanicilariGetir();
        assertThat(users).hasSize(4);
    }

    @Test
    @DisplayName("Projection: Sadece kullanıcı isimlerini DTO olarak getirmeli")
    void findUserNames_ShouldReturnUserNameResponseList() {
        List<UserNameResponse> userNames = userRepository.findUserNames();
        assertThat(userNames).isNotEmpty();
        assertThat(userNames.stream().filter(u -> u.name() != null).count()).isEqualTo(3);
        assertThat(userNames.get(0)).isInstanceOf(UserNameResponse.class);
    }

    @Test
    @DisplayName("Search: Prefix ile case-insensitive arama yapmalı")
    void searchByPrefix_ShouldReturnMatchingUsers() {
        List<User> result = userRepository.searchByPrefix("ah");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ahmet");
    }

    @Test
    @DisplayName("Search: İsim prefix ve yaş kriterine göre DTO dönmeli")
    void searchByNameAndAge_ShouldReturnUserSearchDto() {
        List<UserSearchDto> result = userRepository.searchByNameAndAge("me", 30);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Mehmet");
        assertThat(result.get(0).age()).isEqualTo(30);
    }

    @Test
    @DisplayName("Order: Maaşa göre azalan sıralama yapmalı")
    void maasaGoreAzalanSiraylaGetir_ShouldReturnOrderedList() {
        List<User> result = userRepository.maasaGoreAzalanSiraylaGetir();
        assertThat(result.get(0).getName()).isEqualTo("Ayşe");
        assertThat(result.get(result.size() - 1).getMaas()).isEqualTo(40000.0);
    }

    @Test
    @DisplayName("IN Clause: Liste içindeki isimlere göre getirmeli")
    void findUsers_ShouldReturnUsersInList() {
        List<String> searchNames = Arrays.asList("Ahmet", "Ayşe", "OlmayanBiri");
        List<User> result = userRepository.findUsers(searchNames);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName).contains("Ahmet", "Ayşe");
    }

    @Test
    @DisplayName("Count: Belirli yaştan büyükleri saymalı")
    void countUsers_ShouldReturnCorrectCount() {
        Long count = userRepository.countUsers(28);
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("Between: Maaş aralığına göre getirmeli")
    void findMaasAraligi_ShouldReturnUsersInSalaryRange() {
        List<User> result = userRepository.findMaasAraligi(45000.0, 65000.0);
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getName).contains("Ahmet", "Mehmet");
    }

    @Test
    @DisplayName("Is Null: İsmi null olan kullanıcıları getirmeli")
    void findUserByNameIsNull_ShouldReturnUsersWithNullName() {
        List<User> result = userRepository.findUserByNameIsNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAge()).isEqualTo(40);
    }

    @Test
    @DisplayName("Sum: Maaşların toplamını hesaplamalı")
    void getMaasSum_ShouldReturnTotalSalary() {
        Double sum = userRepository.getMaasSum();
        assertThat(sum).isEqualTo(225000.0);
    }

}
