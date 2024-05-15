package com.NaSos.RandoMath.service;

import com.NaSos.RandoMath.model.CliAuth;
import com.NaSos.RandoMath.model.Client;
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



    public final ClientService clientService;
    @Autowired
    public JustController(ClientService clientService) {
        this.clientService = clientService;
    }


    @RequestMapping (value ="/greeting",method = RequestMethod.GET)
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "dd";
    }
    @GetMapping("/addclient")
    public String clientForm(Model model) {
        model.addAttribute("client", new Client());
        return "greeting";
    }

    @PostMapping("/goida")
    public String clientResult(@ModelAttribute Client client, Model model) {
        clientService.create(client);
        ResponseEntity<?> n = new ResponseEntity<>(HttpStatus.CREATED);
        System.out.println(n);
        model.addAttribute("clientid1", client.getId());
        model.addAttribute("clientpoints1", client.getPoints());
        model.addAttribute("clientpassword1", client.getPassword());
        model.addAttribute("clientname1", client.getName());
        model.addAttribute("clientphone1", client.getPhone());
        model.addAttribute("clientemail1", client.getEmail());


        return "result";
    }

    @GetMapping("/authorize")
    public String authorize(Model model){
        model.addAttribute("cliauth", new CliAuth());
        return "authorize";
    }

    @PostMapping("/authorize")
    public String authorized(@ModelAttribute CliAuth cliauth, Model model){
        int access = 0;
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
            return "authorize";
        }

    }

}