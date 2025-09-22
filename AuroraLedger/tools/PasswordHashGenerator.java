import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder(12);
        String raw = "SenhaForte123!";
        System.out.println(enc.encode(raw));
    }
}










