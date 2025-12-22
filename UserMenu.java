import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class UserMenu {
    // ==== Config ====
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final long LOCKOUT_COOLDOWN_MS = 30_000;
    private static final boolean ENABLE_EXP_BACKOFF = true;
    private static final long BACKOFF_BASE_MS = 1000;

    private static final Scanner SC = new Scanner(System.in);

    public static void main(String[] args) {
        UserStore store = new UserStore();
        seedDemoUsers(store);

        System.out.println("=== Welcome to the User Menu System ===");
        while (true) {
            AuthResult auth = loginWithRetry(store);
            if (!auth.success) {
                System.out.println("Goodbye.");
                break;
            }

            runUserSession(store, auth.username);

            System.out.print("Exit application? (y/n): ");
            String exit = SC.nextLine().trim().toLowerCase();
            if (exit.startsWith("y")) {
                System.out.println("Goodbye.");
                break;
            }
        }
    }

    private static AuthResult loginWithRetry(UserStore store) {
        int attempts = 0;
        long lockedUntil = 0L;

        while (true) {
            long now = System.currentTimeMillis();
            if (now < lockedUntil) {
                long waitMs = lockedUntil - now;
                System.out.println("Too many failed attempts. Locked for " + waitMs / 1000 + "s...");
                sleep(waitMs);
            }

            System.out.println("\n--- Login ---");
            System.out.print("Username (or 'exit'): ");
            String user = SC.nextLine().trim();
            if ("exit".equalsIgnoreCase(user)) return AuthResult.fail();

            System.out.print("Password: ");
            String pass = SC.nextLine();

            boolean ok = store.verify(user, pass);
            if (ok) {
                System.out.println("Login successful. Welcome, " + user + "!");
                return AuthResult.success(user);
            } else {
                attempts++;
                System.out.println("Invalid credentials. Attempt " + attempts + " of " + MAX_LOGIN_ATTEMPTS + ".");
                if (ENABLE_EXP_BACKOFF) {
                    long backoffMs = BACKOFF_BASE_MS * attempts; // linear; switch to exponential if needed
                    System.out.println("Applying backoff: " + backoffMs + "ms.");
                    sleep(backoffMs);
                }

                if (attempts >= MAX_LOGIN_ATTEMPTS) {
                    lockedUntil = System.currentTimeMillis() + LOCKOUT_COOLDOWN_MS;
                    System.out.println("Max attempts reached. Locking for " + LOCKOUT_COOLDOWN_MS / 1000 + "s.");
                    // Reset attempts after lockout window
                    attempts = 0;
                }
            }
        }
    }

    private static void runUserSession(UserStore store, String username) {
        while (true) {
            System.out.println("\n=== User Menu (" + username + ") ===");
            System.out.println("1. View profile");
            System.out.println("2. Change password");
            System.out.println("3. Logout");
            System.out.print("Choose: ");

            String choice = SC.nextLine().trim();
            switch (choice) {
                case "1":
                    viewProfile(store, username);
                    break;
                case "2":
                    changePassword(store, username);
                    break;
                case "3":
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewProfile(UserStore store, String username) {
        Optional<User> opt = store.get(username);
        if (opt.isEmpty()) {
            System.out.println("User not found.");
            return;
        }
        User u = opt.get();
        System.out.println("--- Profile ---");
        System.out.println("Username: " + u.username);
        System.out.println("Created: " + u.createdHuman());
        System.out.println("Roles: " + String.join(", ", u.roles));
    }

    private static void changePassword(UserStore store, String username) {
        System.out.print("Current password: ");
        String current = SC.nextLine();
        if (!store.verify(username, current)) {
            System.out.println("Current password incorrect.");
            return;
        }
        System.out.print("New password: ");
        String newPass = SC.nextLine();
        System.out.print("Confirm new password: ");
        String confirm = SC.nextLine();

        if (!newPass.equals(confirm)) {
            System.out.println("Passwords do not match.");
            return;
        }
        if (newPass.length() < 8) {
            System.out.println("Password too short (min 8 chars).");
            return;
        }
        store.updatePassword(username, newPass);
        System.out.println("Password updated successfully.");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private static void seedDemoUsers(UserStore store) {
        store.create("admin", "Admin@123", List.of("ADMIN"));
        store.create("rithwik", "Rithwik@123", List.of("USER"));
        store.create("guest", "Guest@123", List.of("GUEST"));
    }

    private record AuthResult(boolean success, String username) {
        static AuthResult success(String u) {
            return new AuthResult(true, u);
        }

        static AuthResult fail() {
            return new AuthResult(false, null);
        }
        }

    private static class User {
        final String username;
        String passwordHash;
        final long createdAt;
        final List<String> roles;

        User(String username, String passwordHash, List<String> roles) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.roles = new ArrayList<>(roles);
            this.createdAt = System.currentTimeMillis();
        }

        String createdHuman() {
            long ageMs = System.currentTimeMillis() - createdAt;
            long minutes = Duration.ofMillis(ageMs).toMinutes();
            return minutes + " minutes ago";
        }
    }

    private static class UserStore {
        private final Map<String, User> users = new HashMap<>();

        void create(String username, String rawPassword, List<String> roles) {
            String hash = hashPassword(rawPassword);
            users.put(username.toLowerCase(), new User(username, hash, roles));
        }

        Optional<User> get(String username) {
            return Optional.ofNullable(users.get(username.toLowerCase()));
        }

        boolean verify(String username, String rawPassword) {
            User u = users.get(username.toLowerCase());
            if (u == null) return false;
            return Objects.equals(u.passwordHash, hashPassword(rawPassword));
        }

        void updatePassword(String username, String newRawPassword) {
            User u = users.get(username.toLowerCase());
            if (u != null) {
                u.passwordHash = hashPassword(newRawPassword);
            }
        }

        private static String hashPassword(String raw) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte b : digest) sb.append(String.format("%02x", b));
                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException("Hashing failed", e);
            }
        }
    }
}