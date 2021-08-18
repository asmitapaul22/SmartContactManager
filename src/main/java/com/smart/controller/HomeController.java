package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.message;

@Controller
public class HomeController {
@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
//	@GetMapping("/test")
//	@ResponseBody
//	public String test()
//	{
//		User user=new User();
////		user.setId(10);
//		user.setName("asmita");
//		user.setEmail("asmita@gmail.com");
//		
//		
//		Contact contact=new Contact();
//		user.getContacts().add(contact);
//		userRepository.save(user);
//		return "working";
//	}
	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title","Home - Smart Contact Manager");
		return "home";	}

@RequestMapping("/about")
public String about(Model m)
{
	m.addAttribute("title","About  - Smart Contact Manager");
	return "about";	}


@RequestMapping("/signup")
public String signup(Model m)
{
	
	m.addAttribute("title","Signup  - Smart Contact Manager");
	m.addAttribute("user", new User());
	return "signup";	}
///handler for registering user
@PostMapping("/do_register")
public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1, @RequestParam(value = "agreement",defaultValue = "false") boolean agreement,Model model,HttpSession session)
{
try {
	if(result1.hasErrors())
	{
		System.out.println(result1);
		return "signup";
	}
	if(!agreement)
	{
		System.out.println("you have not agreed the terms and conditions");
		throw new Exception("you have not agreed the terms and conditions");
	}
	user.setRole("ROLE_USER");
	user.setEnabled(true);
	user.setImageUrl("default.png");
	
	user.setPassword(passwordEncoder.encode(user.getPassword()));
	
	System.out.println("agreement"+agreement);
	System.out.println("USER"+user);
	User result = this.userRepository.save(user);
	model.addAttribute("user",new User());
	session.setAttribute("message",new message("Successfully Registered !!","alert-success"));
	return "signup";
} catch (Exception e) {
	// TODO: handle exception
	e.printStackTrace();
	model.addAttribute("user",user);
	session.setAttribute("message",new message("something went wrong !!"+e.getMessage(),"alert-danger"));
	return "signup";
}
	
}

//handler for custom login
@GetMapping("/login")
public String login() {
	return "login";
}
}