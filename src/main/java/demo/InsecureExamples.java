package demo;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * INTENTIONALLY INSECURE CODE - for testing security scanners only.
 * Do not copy any of this into a real application.
 *
 * Each method is a classic OWASP Top 10 pattern that a SAST tool (Semgrep)
 * should flag when it scans the source.
 */
final class InsecureExamples {

    private InsecureExamples() {}

    /** A03 Injection (XSS): unescaped user input written into HTML. */
    static String greetingHtml(String name) {
        return "<html><body><h1>Hello, " + name + "!</h1></body></html>";
    }

    /** A03 Injection (SQL): query built by string concatenation instead of a PreparedStatement. */
    static ResultSet findUser(Connection conn, String name) throws SQLException {
        Statement st = conn.createStatement();
        return st.executeQuery("SELECT * FROM users WHERE name = '" + name + "'");
    }

    /** A02 Cryptographic Failures: MD5 is a broken hash and unsuitable for passwords. */
    static byte[] hashPassword(String password) throws Exception {
        return MessageDigest.getInstance("MD5").digest(password.getBytes());
    }
}
