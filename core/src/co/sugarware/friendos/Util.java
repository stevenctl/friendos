package co.sugarware.friendos;

public class Util {

    // truncatePath removes the last part of a local file path
    public static String truncatePath(String path) {
        String[] parts = path.split("/");
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            truncated.append(parts[i]);
            if (i < parts.length - 2) {
                truncated.append("/");
            }
        }

        return truncated.toString();
    }

}
