package com.misiontic.g1.securityBackend.services;

import com.misiontic.g1.securityBackend.models.User;
import com.misiontic.g1.securityBackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    /**
     * all user
     */
    public List<User> index(){
        return (List<User>) this.userRepository.findAll();
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<User> show(int id){
        return this.userRepository.findById(id);
    }

    /**
     *
     * @param newUser
     * @return
     */
    public User create(User newUser){
        //Validate if id is present in json
        if(newUser.getId() == null) {
            //create
            if (newUser.getEmail() != null && newUser.getNickname() != null && newUser.getPassword() != null){
                newUser.setPassword(this.convertToSHA256(newUser.getPassword()));
                return this.userRepository.save(newUser);
            }
            else{
                // TODO return 400 code BadRequest
                return newUser;
            }
        }
        else{
            //TODO validate if exist, is not create
            return newUser;
        }
    }

    /**
     *
     * @param id
     * @param updatedUser
     * @return
     */

    public User update(int id, User updatedUser){
        if(id > 0){
            Optional<User> tempUser = this.show(id);
            if(tempUser.isPresent()){
                // Email must not be updated
                if(updatedUser.getNickname() != null)
                    tempUser.get().setNickname(updatedUser.getNickname());
                if(updatedUser.getPassword() != null)
                    tempUser.get().setPassword(updatedUser.getPassword());
                return this.userRepository.save(tempUser.get());
            }
            else{
                // TODO Error 404 NotFound
                return updatedUser;
            }
        }
        else {
            // TODO return 400 code BadRequest, no ID
            return updatedUser;
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean delete(int id){
        Boolean success = this.show(id).map(user -> {
            this.userRepository.delete(user);
            return true;
        }).orElse(false);
        return success;
    }

    /**
     *
     * @param user
     * @return
     */

    public HashMap<String, Boolean> login(User user){
        String email = user.getEmail();
        String password = this.convertToSHA256(user.getPassword());
        Optional<User> result = this.userRepository.login(email, password);
        //TODO improve
        HashMap<String, Boolean> response = new HashMap();
        if(result.isEmpty())
            response.put("login", false);
        else
            response.put("login", true);
        return response;
    }

    /**
     *
     * @param password
     * @return
     */

    public String convertToSHA256(String password){
        MessageDigest md =null;
        try{
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for(byte b : hash){
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
