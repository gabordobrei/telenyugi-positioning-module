package hu.bme.emt.telenyugi.server.msg;

import hu.bme.emt.telenyugi.model.User;

public class UserMessage extends User{
    
    public UserMessage(User user) {
        super(user.getID(), user.getName());
    }
    
}
