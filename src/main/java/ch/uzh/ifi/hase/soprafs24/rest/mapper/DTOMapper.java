package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {
    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id",target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "creationDate", target = "creationDate",dateFormat = "dd.MM.yyyy")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "status", target = "status")
    UserDTO convertEntityToUserDTO(User user);
    //I need User->UserDTO, User->UserGetDTO, UserPostDTO -> User, UserPutDTO->User

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username",target = "username")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "birthday", target = "birthday", dateFormat = "dd.MM.yyyy")
    @Mapping(source = "status", target = "status")
    User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);
}
