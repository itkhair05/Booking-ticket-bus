package util;

import java.util.Random;

public class Utils {
    public static String taoMaVe() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder maVe = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            maVe.append(characters.charAt(random.nextInt(characters.length())));
        }
        return maVe.toString();
    }
}