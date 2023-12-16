/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import java.io.Serializable;

/**
 *
 * @author user
 */
public class DTOPlayer implements Serializable{
    private String userName;
    private String email;
    private String password;

    public DTOPlayer() {
    }
    

    public DTOPlayer(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }
    

    public DTOPlayer(String userName, String passWord) {
        this.userName = userName;
        this.password = passWord;
    }
    
    

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    
}
