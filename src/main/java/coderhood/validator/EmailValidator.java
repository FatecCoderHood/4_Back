package coderhood.validator;

import java.util.regex.Pattern;

public class EmailValidator {

    private EmailValidator() {
    }

    public static boolean isValidEmail(String email) {
        String pattern = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        return Pattern.matches(pattern, email);
    }
}