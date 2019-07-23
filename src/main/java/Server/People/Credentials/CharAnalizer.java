package Server.People.Credentials;

public class CharAnalizer {

    public boolean validatePassword(String password){
        if(password.length() >= 8) {
            if (password.length() <= 16) {
                if (isUpperCase(password)) {
                    if (isDigit(password)) {
                        if (isSpecialChar(password)) {
                            return true;
                        } else {
                            System.out.println("PASSWORD REJCTED: SPECIAL CHARACTER REQUIRED");
                            return false;
                        }
                    } else {
                        System.out.println("PASSWORD REJECTED: NUMBER REQUIRED");
                        return false;
                    }
                } else {
                    System.out.println("PASSWORD REJECTED: UPPER CASE CHARACTER REQUIRED");
                    return false;
                }
            } else {
                System.out.println("PASSWORD REJECTED: MAXIMUM DIMENSION ALLOWED 16 CHARACTERS");
                return false;
            }
        } else {
            System.out.println("PASSWORD REJECTED: MINIMUM DIMESION ALLOWED 8 CHARACTERS");
            return false; }
    }


    protected boolean isLetter(String line) {
        for(char c: line.toCharArray()) {
            if(Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }
    private boolean isUpperCase(String line) {
        for(char c: line.toCharArray()) {
            if(Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }
    private boolean isDigit(String line) {
        for(char c: line.toCharArray()) {
            if(Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
    private boolean isSpecialChar(String line) {
        for(char c: line.toCharArray()) {
            if(!Character.isLetter(c) && !Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }


    public CharAnalizer() { }
}