
package hu.bme.emt.telenyugi.server.msg;

import hu.bme.emt.telenyugi.model.User;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class UserList {

    private List<UserMessage> users;

    public UserList(Collection<User> users) {
        this.users = Lists.newArrayList();
        for (User user : users) {
            this.users.add(new UserMessage(user));
        }
    }

    public List<UserMessage> getUsers() {
        return users;
    }

}
