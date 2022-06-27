package com.catalogue.others;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Pattern;

@Component
public class AppUtils {

    public String stringGenerator(byte length,byte type){
        StringBuilder id= new StringBuilder();
        String randomChars = "";

        //there are different types because it is use as the general random generator
        switch (type){
            case 1: randomChars="ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
                break;
            case 2: randomChars="123456789";
                break;
            case 3: randomChars="12,>34<W5sa789&%*-_!@=+/|AV";
        }
        for (int i = 0; i < length; i++) {
            id.append(randomChars.charAt(new Random().nextInt(randomChars.length()-1)));
        }
        return id.toString();
    }

    /**
     * <code><p>This method encrypts specific message before storing to database. Here is how it works - </p></br>
     * <li>First, the front end guy  gives a plain password over an HTTPS protocol</li>
     * <li>Then I hash this message+salt with SHA256 algorithm and save (Signup) or compare (SignIn) to database</li>
     *  </code>
     * @param saltedMessage the ciphered text (encoded in base64)
     * @param  secret secret used to encrypt
     * @return the an string(base64 encode of hash)
     * @throws NoSuchAlgorithmException the process may throw exception
     * @throws InvalidKeyException the process may throw exception
     */

    public String hash(String secret, String saltedMessage) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.encodeBase64String(sha256_HMAC.doFinal(saltedMessage.getBytes()));
    }

    public String validateField(String field, String type){
        switch (type){
            case "password" :
                if (field.length()==0)
                    return "This field is required";

                else if (field.length()<8)
                    return "Password should be at least 8 characters in length";

                byte specialCharCounter=0;
                for (int i = 0; i < field.length(); i++) {
                    if (Character.isWhitespace(field.charAt(i)))
                        return "Password should not contain white spaces";

                    if (!Character.isDigit(field.charAt(i)) && !Character.isLetter(field.charAt(i)))
                        specialCharCounter++;
                }
                if (specialCharCounter==0)
                    return "Password should contain at least one special character";

                return "Password is valid";
            case "DOB" :
                int birthYear=Integer.parseInt(field.split(" ")[2]); //field.split returns array
                if (birthYear>=1962 && birthYear<=2004) //means such user is too old, 1962 is a constant, not good as time goes on
                    return "Date of  birth is valid";
                else
                    return "Date of birth not eligible for record keeping";

            case "name" :
                if (field.length()==0)
                    return "This field is required";

                byte letterCounter=0;
                for (int i = 0; i < field.length(); i++) {

                    if (Character.isLetter(field.charAt(i)))
                        letterCounter++;
                }
                if (field.length()-letterCounter==2)
                    return "Name is valid";
                else
                    return "Name should be in order - First name->Middle name->Last name";

            case "email" :
                if (field.length()==0) {
                    return "This field is required";
                }
                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +"[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                Pattern pattern = Pattern.compile(emailRegex);
                if (pattern.matcher(field).matches()) {
                    return "Email is valid";
                }
                else
                    return "Email format is not correct";

            case "phone":
                if (field.length()==0)
                    return "This field is required";
                byte phoneLengthCounter=0;
                if(field.length()==11 || field.length()==14){
                    if (field.startsWith("070") || field.startsWith("080") || field.startsWith("081") || field.startsWith("090")
                            || field.startsWith("091") || field.startsWith("+234")){
                        for (int i = 3; i < field.length(); i++) {
                            if (Character.isDigit(field.charAt(i)))
                                phoneLengthCounter++;
                        }
                    }
                    else return "Phone number format not accepted";
                    if (phoneLengthCounter<=11)
                        return "Phone number is valid"; //validation
                    else
                        return  "Phone number format not accepted";
                }
                else return "Phone number should contain at most eleven digits";

            case "address":
                if (field.length()==0)
                    return "address field is required";
                if (!Character.isLetterOrDigit(field.charAt(0)))
                    return "address is not authentic";

                for (int i = 1; i < field.length(); i++) {
                    if (Character.isLetterOrDigit(field.charAt(i)))
                        continue;
                    if (!(field.charAt(i)==' ' || field.charAt(i)==',' || field.charAt(i)=='.'))
                        return "address is not authentic";
                }
                return "address is valid";
        }
        return "";
    }
}
