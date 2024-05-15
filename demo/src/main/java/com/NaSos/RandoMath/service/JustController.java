package com.NaSos.RandoMath.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import com.NaSos.RandoMath.model.CliAuth;
import com.NaSos.RandoMath.model.Client;
import com.NaSos.RandoMath.model.MathThing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
public class JustController {

    public static String encryptThisString(String input) {

        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public final ClientService clientService;
    @Autowired
    public JustController(ClientService clientService) {
        this.clientService = clientService;
    }


    @RequestMapping (value ="/greeting",method = RequestMethod.GET)
    public String greeting( Model model) {
        return "index";
    }
    @GetMapping("/signup")
    public String clientForm(Model model) {
        model.addAttribute("client", new Client());
        return "reg";
    }

    @PostMapping("/signup")
    public String clientResult(@ModelAttribute Client client, Model model) {


        client.setPassword(encryptThisString(client.getPassword()));
        clientService.create(client);
        ResponseEntity<?> n = new ResponseEntity<>(HttpStatus.CREATED);
        model.addAttribute("youcli",client);


        return "menu";
    }

    @GetMapping("/login")
    public String authorize(Model model){
        model.addAttribute("cliauth", new CliAuth());
        return "aut";
    }

    @PostMapping("/login")
    public String authorized(@ModelAttribute CliAuth cliauth, Model model){
        int access = 0;
        cliauth.setPassword(encryptThisString(cliauth.getPassword()));
        for (Client client: clientService.readAll()){
            if (Objects.equals(cliauth.getId(), client.getId()) &&
                    Objects.equals(cliauth.getPassword(), client.getPassword())) {
                access = 1;
                model.addAttribute("youcli", client);
                break;
            }
        }

        if(access == 1){
            return "menu";
        }else{
            return "aut";
        }

    }
    @PostMapping("/easy")
    public String easy(@ModelAttribute Client client, Model model){
        model.addAttribute("cli", client);
        int a = (int) ((Math.random()*2000) -1000);
        int b = (int) ((Math.random()*2000) -1000);
        int c = a + b;
        model.addAttribute("math", "Решите пример: \n("+a+") + ("+b+") = ?");
        model.addAttribute("c", c);
        return "solve";
    }
    @PostMapping("/average")
    public String average(@ModelAttribute Client client, Model model){
        model.addAttribute("cli", client);
        int a = (int) ((Math.random()*600) -300);
        int b = ((int) ((Math.random()*20) -10))*a;
        int c =b/a;
        model.addAttribute("math", "Решите пример: \nx * ("+a+") = "+b);
        model.addAttribute("c", c);
        return "solve";
    }
    @PostMapping("/hard")
    public String hard(@ModelAttribute Client client, Model model){
        model.addAttribute("cli", client);
        int a = (int) ((Math.random()*10) -5);
        if(a == 0){
            a = 1;
        }
        int x1 = (int) ((Math.random()*50) -25);
        int x2 = (int) ((Math.random()*50) -25);
        int c = Math.min(x1,x2);
        model.addAttribute("math", "Найдите меньший корень: \n"+a+" * (x^2) + ("+(a*(-x1-x2))+") * x + ("+(x1 * x2 * a)+") = 0");
        model.addAttribute("c", c);
        return "solve";
    }

    @PostMapping("/check")
    public String check(@ModelAttribute MathThing maththing, Model model){
        Client client = new Client(
                maththing.getId(),
                maththing.getName(),
                maththing.getEmail(),
                maththing.getPassword(),
                maththing.getPhone(),
                maththing.getPoints());

        if(Objects.equals(maththing.getAnswer(), maththing.getC())){
            model.addAttribute("text","Nicely done! Next?");
            client.setPoints(client.getPoints()+1);
            clientService.update(client,client.getId());
        }else{
            model.addAttribute("text", "Wrong answer. Right answer: "+maththing.getC()+". Your answer: "+maththing.getAnswer()+". Next?");
        }
        model.addAttribute("youcli",client);

        return "check";
    }

}