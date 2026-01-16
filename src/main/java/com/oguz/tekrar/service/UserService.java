package com.oguz.tekrar.service;

import com.oguz.tekrar.dto.UserNameResponse;
import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.dto.UserSearchDto;
import com.oguz.tekrar.entity.Role;
import com.oguz.tekrar.entity.User;
import com.oguz.tekrar.mapper.UserMapper;
import com.oguz.tekrar.repository.RoleRepository;
import com.oguz.tekrar.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toUserResponseList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        Role role = new Role();
        role.setRoleName(DEFAULT_ROLE);
        role.setUser(user);
        if (user.getRoles() != null) {
            user.getRoles().add(role);
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse updateUser(UserRequest userRequest, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot update. User not found with id: " + id));
        userMapper.updateEntityFromRequest(userRequest, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    // bu metod'un buraya yazılması doğru değil normal şartlarda roleService sınıfı oluşturulup oraya yazılmalı
    @Transactional
    public void deleteRole(Long roleId) {
        Role rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + roleId));
        if (rol.getUser() != null) {
            User user = rol.getUser();
            user.getRoles().remove(rol);
            rol.setUser(null);
            userRepository.save(user);
            // roleRepository.delete(rol); eğer orphanRemoval true yapmasaydık bunu da yazacaktık true olduğu için gerek yok
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersCustom() {
        List<User> users = userRepository.tumKullanicilariGetir();
        return userMapper.toUserResponseList(users);
    }

    @Transactional(readOnly = true)
    public List<UserNameResponse> getUserNames() {
        return userRepository.findUserNames();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String prefix) {
        return userMapper.toUserResponseList(userRepository.searchByPrefix(prefix));
    }

    @Transactional(readOnly = true)
    public List<UserSearchDto> searchUsersByNameAndAge(String prefix, Integer age) {
        return userRepository.searchByNameAndAge(prefix, age);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersBySalaryDesc() {
        return userMapper.toUserResponseList(userRepository.maasaGoreAzalanSiraylaGetir());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findUsersByNames(List<String> names) {
        return userMapper.toUserResponseList(userRepository.findUsers(names));
    }

    @Transactional(readOnly = true)
    public Long findCountByAge(Integer age) {
        return userRepository.countUsers(age);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findMaasAraligi(Double min, Double max) {
        var user = userRepository.findMaasAraligi(min, max); // bu şekilde var kullanımıyla da kullanım mevcut List<User> yazmak yerine bunu yazabiliriz
        return userMapper.toUserResponseList(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAdiIsNull() {
        var user = userRepository.findUserByNameIsNull();
        return userMapper.toUserResponseList(user);
    }

    @Transactional(readOnly = true)
    public Double getMaasSum() {
        return userRepository.getMaasSum();
    }
}