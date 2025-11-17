package at.technikum.application.mrp.middleware;

import at.technikum.application.mrp.model.User;

public class RequestContext {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    public static User getCurrentUser() {
        return currentUser.get();
    }

    public static String getCurrentUserId() {
        User user = currentUser.get();
        return user != null ? user.getId() : null;
    }

    public static void clear() {
        currentUser.remove();
    }
}
// threadlocal um den user pro request zu speichern, sonst kanns mixups geben bei mehreren requests gleichzeitig