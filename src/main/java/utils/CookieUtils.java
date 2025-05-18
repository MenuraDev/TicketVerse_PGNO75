package utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CookieUtils {

    public static final String USER_COOKIE_NAME = "userLogin";
    public static final String ADMIN_COOKIE_NAME = "adminLogin"; // Separate cookie for admins
    public static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30 days

    // Create cookie based on role
    public static void createLoginCookie(HttpServletResponse response, String username, String role) {
        String cookieName;// Define cookieName variable
        if ("admin".equalsIgnoreCase(role)) {
            cookieName = ADMIN_COOKIE_NAME;
        }else {
            cookieName = USER_COOKIE_NAME;
        }

        Cookie cookie = new Cookie(cookieName, username);
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
    // Delete both user and admin cookies
    public static void deleteLoginCookie(HttpServletResponse response) {
        deleteCookie(response, USER_COOKIE_NAME);
        deleteCookie(response, ADMIN_COOKIE_NAME);
    }

    // Helper method for deleting a cookie
    private static void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    //gets cookie value of both user and admin
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}