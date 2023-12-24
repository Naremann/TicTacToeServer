/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import java.io.Serializable;

/**
 *
 * @author DELL
 */
public class DTORequest implements Serializable{
    private String userNameSender;
    private String userNameReceiver;

    
    public DTORequest(){}
    
    public DTORequest(String userNameSender, String userNameReceiver) {
        this.userNameSender = userNameSender;
        this.userNameReceiver = userNameReceiver;
    }

    public DTORequest(String senderUserName) {
        this.userNameSender = userNameSender;
    }

    public String getUserNameSender() {
        return userNameSender;
    }

    public String getUserNameReceiver() {
        return userNameReceiver;
    }

    public void setUserNameSender(String userNameSender) {
        this.userNameSender = userNameSender;
    }

    public void setUserNameReceiver(String userNameReceiver) {
        this.userNameReceiver = userNameReceiver;
    }
    
   
}
