package com.oguz.tekrar.mapper;

import com.oguz.tekrar.dto.UserRequest;
import com.oguz.tekrar.dto.UserResponse;
import com.oguz.tekrar.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toUser(UserRequest userRequest);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);

    void updateEntityFromRequest(UserRequest request, @MappingTarget User entity);
}
