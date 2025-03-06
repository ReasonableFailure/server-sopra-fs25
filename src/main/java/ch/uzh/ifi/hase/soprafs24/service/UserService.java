package ch.uzh.ifi.hase.soprafs24.service;

import ch.qos.logback.core.status.StatusUtil;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    checkIfUserExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
      //User userByUsername = userRepository.findByPassword(userToBeCreated.getPassword());
      User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

      String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
      if (userByUsername != null) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
      }
  }

  public User findByID(Long ID){
      List<User> userList = getUsers();
      for(User comp : userList){
          if(comp.getId() == ID){
              return comp;
          }
      }
      return null;
  }
    public User findByName(String name) {
        List<User> userList = getUsers();
        for (User comp : userList) {
            if (comp.getUsername().equals(name)) {
                return comp;
            }
        }
        return null;
    }
    public User logIn(User logInUser){
       //TODO: Find user by Username in JPA repo. If not found Throw StatusResponseException. Check if password matches password on file. yes -> return User. no -> Throw ResponseStatusException
        User found = findByName(logInUser.getUsername());
        if(found == null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not exist");
        }
        if(found.getPassword().equals(logInUser.getPassword())){
            //set to online and assign auth token
            found.setStatus(UserStatus.ONLINE);
            found.setToken(UUID.randomUUID().toString());
            return found;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Wrong Password");
        }
    }

    public void editUser(User editUser){
      User toEdit = findByID(editUser.getId());
      if(toEdit == null){
          throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("user with ID %d was not found",editUser.getId()));
      }
      if(!editUser.getToken().equals(toEdit.getToken())){ //yes putDTO hast token
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised Access");
      }
      User preExisting = findByName(editUser.getUsername());
      if(preExisting != null){
          throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
      toEdit.setUsername(editUser.getUsername());
      toEdit.setBirthday(editUser.getBirthday());
      return;
    }

//    public User viewUser(User toShow){
//      return new User();
//    }

    public User logOutUser(User entity){
      User toLogOut = findByID(entity.getId());
      if(!entity.getToken().equals(toLogOut.getToken()) || toLogOut.getToken() == null){
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised Access");
      }
      //user might be offline already
      if(toLogOut.getStatus().equals(UserStatus.OFFLINE)){
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User already logged out!");
      }
//      final String invalidToken = "Invalid Token";
      toLogOut.setStatus(UserStatus.OFFLINE);
      toLogOut.setToken(null);
      return toLogOut;
    }
}
