package test;

import auth.AuthFactory;
import auth.AuthMethod;

public class Demo {

    public static void main(String[] args) {

        AuthMethod auth1 = AuthFactory.create("OTP");
        auth1.authenticate("client1");

        AuthMethod auth2 = AuthFactory.create("PASSWORD");
        auth2.authenticate("client2");
    }
}
