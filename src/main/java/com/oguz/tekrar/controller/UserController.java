package com.oguz.tekrar.controller;

import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
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
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping(GET_BY_ID)
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping(CREATE)
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.create(userRequest));
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(UPDATE)
    public ResponseEntity<UserResponse> updateUserById(@RequestBody UserRequest userRequest, @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(userRequest, id));
    }

    @DeleteMapping(DELETE_ROLE)
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId){
        userService.deleteRole(roleId);
        return ResponseEntity.ok().build();
    }
}
