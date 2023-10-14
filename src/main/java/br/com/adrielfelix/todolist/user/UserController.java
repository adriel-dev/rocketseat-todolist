package br.com.adrielfelix.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private UserRepository userRepository;
 
    @PostMapping("/register")
    public ResponseEntity<Object> create(@RequestBody UserModel userModel) {
        var username = userModel.getUsername();
        var foundUser = this.userRepository.findByUsername(username).isPresent();
        if(foundUser) {
            return badRequest().body("User with username ["+username+"] already exists!");
        }
        var encryptedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(encryptedPassword);
        var createdUser = this.userRepository.save(userModel);
        var userId = createdUser.getId();
        return created(URI.create("/api/v1/user/"+userId))
                .body(createdUser);
    }

}