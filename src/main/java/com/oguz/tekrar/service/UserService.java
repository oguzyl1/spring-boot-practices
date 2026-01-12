package com.oguz.tekrar.service;

import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.entity.Role;
import com.oguz.tekrar.entity.User;
import com.oguz.tekrar.mapper.UserMapper;
import com.oguz.tekrar.repository.RoleRepository;
import com.oguz.tekrar.repository.UserRepository;
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

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseList(users);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Aranan Id ile kullanıcı bulunamadı."));
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse create(UserRequest userRequest) {
        User user = userMapper.toUser(userRequest);
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        role.setUser(user);
        user.getRoles().add(role);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Silinmek istenen id ile kullanıcı bulumadı."));
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse updateUser(UserRequest userRequest, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Güncellenmek istenen id ile kullanıcı bulunamadı."));
        userMapper.updateEntityFromRequest(userRequest, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toUserResponse(updatedUser);
    }


    // bu metod'un buraya yazılması doğru değil normal şartlarda roleService sınıfı oluşturulup oraya yazılmalı
    @Transactional
    public void deleteRole(Long roleId) {
        Role rol = roleRepository.findById(roleId).orElse(null);
        if (rol != null && rol.getUser() != null) {
            User user = rol.getUser();
            user.getRoles().remove(rol);
            rol.setUser(null);
            userRepository.save(user);
            // roleRepository.delete(rol); eğer orphanRemoval true yapmasaydık bunu da yazacaktık true olduğu için gerek yok
        }
    }


}
