package MovieBooking.model;

/**
 * Admin class contains hardcoded admin credentials
 * Admin cannot register through normal signup process
 */
public class Admin {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@moviebooking.com";
    private static final String ADMIN_PASSWORD = "admin123";
    
    // Check if login credentials match admin account

    /**
     *
     * @param identifier
     * @param password
     * @return
     */
    public static boolean isAdmin(String identifier, String password) {
        return (identifier.equals(ADMIN_USERNAME) || identifier.equals(ADMIN_EMAIL)) 
               && password.equals(ADMIN_PASSWORD);
    }
    
    // Get admin username

    /**
     *
     * @return
     */
    public static String getAdminUsername() {
        return ADMIN_USERNAME;
    }
    
    // Get admin email

    /**
     *
     * @return
     */
    public static String getAdminEmail() {
        return ADMIN_EMAIL;
    }
}