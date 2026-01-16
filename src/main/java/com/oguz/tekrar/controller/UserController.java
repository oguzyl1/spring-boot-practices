package com.oguz.tekrar.controller;

import com.oguz.tekrar.dto.UserNameResponse;
import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.dto.UserSearchDto;
import com.oguz.tekrar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.oguz.tekrar.constant.UserApiPath.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(GET_ALL)
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(GET_BY_ID)
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping(CREATE)
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @PutMapping(UPDATE)
    public ResponseEntity<UserResponse> updateUserById(@RequestBody UserRequest userRequest, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(userRequest, id));
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(DELETE_ROLE)
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        userService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(TUM_KULLANICILARI_GETIR)
    public ResponseEntity<List<UserResponse>> getAllUsersCustom() {
        return ResponseEntity.ok(userService.getAllUsersCustom());
    }

    @GetMapping(GET_USER_NAMES)
    public ResponseEntity<List<UserNameResponse>> getUserNames() {
        return ResponseEntity.ok(userService.getUserNames());
    }

    @GetMapping(SEARCH_BY_PART)
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam("name") String prefix) {
        return ResponseEntity.ok(userService.searchUsers(prefix));
    }

    @GetMapping(SEARCH_ADVANCED)
    public ResponseEntity<List<UserSearchDto>> searchAdvanced(
            @RequestParam("name") String prefix,
            @RequestParam("age") Integer age) {
        return ResponseEntity.ok(userService.searchUsersByNameAndAge(prefix, age));
    }

    @GetMapping(GET_MAAS_AZALAN_SIRALAMA)
    public ResponseEntity<List<UserResponse>> getUsersBySalaryDesc() {
        return ResponseEntity.ok(userService.getUsersBySalaryDesc());
    }

    @PostMapping(FIND_USERS_BY_ISIMLER)
    public ResponseEntity<List<UserResponse>> findUsersByNames(@RequestBody List<String> names) {
        return ResponseEntity.ok(userService.findUsersByNames(names));
    }

    @GetMapping(COUNT_USER_BY_AGE)
    public ResponseEntity<Long> findUserCountByAge(@RequestParam Integer age) {
        return ResponseEntity.ok(userService.findCountByAge(age));
    }

    @GetMapping(MAAS_ARALIGINA_GORE_GETIR)
    public ResponseEntity<List<UserResponse>> findMaasAraligi(@RequestParam Double max, @RequestParam Double min) {
        return ResponseEntity.ok(userService.findMaasAraligi(min, max));
    }

    @GetMapping(FIND_USERS_NAME_IS_NULL)
    public ResponseEntity<List<UserResponse>> findUsersAdiIsNull() {
        return ResponseEntity.ok(userService.findAdiIsNull());
    }

    @GetMapping(GET_MAAS_SUM)
    ResponseEntity<Double> getMaasSum() {
        return ResponseEntity.ok(userService.getMaasSum());
    }
}