package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	private boolean value=false;
	private int addingcount=0;
	
	private String fileName;
	//method for adding common data to response
	//common method to get data about user , it will be called for each and every handler automatically with the use of @ModelAttribute
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
//		Principal authentication is the process of proving your identity to the security enforcing components of the system so that they can grant access to information and services based on who you are. ... A user or application that can authenticate itself is known as a principal.
		String userName = principal.getName();
	   System.out.println("USERNAME"+userName);
	   //get the user by username(email)
	   User user = userRepository.getUserByUserName(userName);
	   System.out.println("USER"+user);
	   model.addAttribute("user",user);
	}
	
	
	//dashboard home
@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
	String userName = principal.getName();
	   System.out.println("USERNAME"+userName);
	   //get the user by username(email)
	   User user = userRepository.getUserByUserName(userName);
	
	model.addAttribute("title","User Dashboard");
	model.addAttribute("user",user);
	return "normal/user_dashboard";
	}




//open add contact form handler
@GetMapping("/add-contact")
public String openAddContectForm(Model model)
{
	model.addAttribute("title","Add Contact");
	model.addAttribute("contact",new Contact());
	return "normal/add_contact_form";
}

//processing add contact form
@PostMapping("/process-contact")
public String  processContact(@Valid @ModelAttribute Contact contact,BindingResult result1,@RequestParam("profileImage") MultipartFile file,Model model,Principal principal,HttpSession session) {
try {
	
	if(result1.hasErrors())
	{
		System.out.println(result1);
		return "normal/add_contact_form";
	}
	
	
	
	System.out.println("email of contact entered"+contact.getEmail());
//	String email = contact.getEmail();
	String name = principal.getName();
	System.out.println("name by rpincipal"+name);
	User user = this.userRepository.getUserByUserName(name);
	
	
	
	
	System.out.println("EMAIL"+contact.getEmail());
	
	
	ArrayList<Contact> arrayList = new ArrayList<>();
	arrayList.addAll(user.getContacts());
	Iterator<Contact> iter = arrayList.iterator();
	System.out.println("contacts----------------------------------"+iter);
	iter.forEachRemaining(con->{
		System.out.println("email to get if condition -CON"+con.getEmail());
		if(con.getEmail().equals(contact.getEmail())) {
	
//			System.out.println("not added to database");
			addingcount=addingcount+1;
			
			
		}
			});
	System.out.println("addingcount"+addingcount);
	if(addingcount>0)
	{
		System.out.println("not added to database");
		session.setAttribute("message",new message("The email you have entered has already been used .Please try with some another email id. !!","alert-danger"));
		
	}
	if(addingcount==0){
//		System.out.println("---------------------------------");
//		System.out.println("email to get if condition -con"+con.getEmail());
//		System.out.println("email to get if condition -contact"+contact.getEmail());
//		System.out.println("-------------------------------");
		
		//FOR STORING IMAGE
		if(file.isEmpty())
		{
			//	IF THE FILE IS EMPTY
			
		
			
			//or you can store a default img value using--- cpntact.setImage("contact.png");
			value=true;
			model.addAttribute("value",value);
			model.addAttribute("filing","Profile photo is required");
			return "normal/add_contact_form";
			
		}
		else {
			//SAVE THE FILE TO FOLDER AND UPDATE THE NAME TO CONTACT
		
			File saveFile = new ClassPathResource("static/images").getFile();
			String uniqueFileName = contact.getEmail()+file.getOriginalFilename();
			System.out.println("uniquename------"+uniqueFileName);
			System.out.println("contact image email----------"+contact.getEmail());
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+uniqueFileName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			contact.setImage(uniqueFileName);
		
		}
		
		
		//FOR REST OF THE DATA
		contact.setUser(user);
		
		user.getContacts().add(contact);

		this.userRepository.save(user);
		System.out.println("cid after saving contact"+contact.getcId());
		System.out.println("added to database");
		model.addAttribute("contact",new Contact());
		session.setAttribute("message",new message("Contact Successfully Added !!","alert-success"));
	}

	
	
addingcount=0;
	return "normal/add_contact_form";
} catch (Exception e) {
	// TODO: handle exception
	e.printStackTrace();
	model.addAttribute("contact",contact);
	session.setAttribute("message",new message("something went wrong !!"+e.getMessage(),"alert-danger"));

	return "normal/add_contact_form";
}
}
//SHOW CONTACTS HANDLER
// per page=5[n]
//current page = 0 [page] 

@GetMapping("/show-contacts/{page}")
public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal)
{
	model.addAttribute("title", "Show User Contacts");
	// to get the list of contacts of a single user who has logged in----------
	
	//1 WAY
//	String userName = principal.getName();
//	User user = this.userRepository.getUserByUserName(userName);
//	List<Contact> contacts = user.getContacts();
	//and then we can send this list to render on the page....(which is correct)
	//but as we will require contact repository for later purpose thats why we will be retrieving ddata using the contact repo and not by this way.

	
	//2ND WAY
	String userName = principal.getName();
	User user = this.userRepository.getUserByUserName(userName);
	
	
	//pageable is the parent of pagerequest
	// 1. currentPage-page
		//2. Contact Per page =5
	Pageable pageable = PageRequest.of(page, 5);
	
	Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
	model.addAttribute("contacts", contacts);
	model.addAttribute("currentPage",page);
	model.addAttribute("totalPages",contacts.getTotalPages());
	System.out.println(contacts);
	return "normal/show_contacts";
}
@GetMapping("/contact/{cId}")
public String showContactDetails(@PathVariable("cId")Integer cId,Model model,Principal principal) {
	System.out.println(cId);
	Optional<Contact> contactOptional = this.contactRepository.findById(cId);
Contact contact = contactOptional.get();
String userName = principal.getName();
User user = this.userRepository.getUserByUserName(userName);
//check
if(user.getId()==contact.getUser().getId())
{
	model.addAttribute("title",contact.getName());
	model.addAttribute("contact_detail",contact);
}

	return "normal/contact_detail";
}

//DELETE CONTACT HANDLER
@GetMapping("/delete/{cId}")
public String deleteContact(@PathVariable("cId") Integer cId,Principal principal,HttpSession session)
{
	String userName = principal.getName();
	User user = this.userRepository.getUserByUserName(userName);
	
 Contact contact = this.contactRepository.findById(cId).get();
	
	//check
	if(user.getId()==contact.getUser().getId())
	{
		//we cannot directly delete a contact as it is interlinked with user entity
		//to unlink with user 
		//deleted specific contact from user
		boolean remove = user.getContacts().remove(contact);
		System.out.println("removeee"+remove);
		//deleted the userid of that perticular contact
		contact.setUser(null);
		this.contactRepository.delete(contact);
		System.out.println("");
		session.setAttribute("message",new message("Contact Successfully Deleted !!!", "alert-success"));
	}
	
	return "redirect:/user/show-contacts/0";
}


//UPDATE FORM HANDLER
@GetMapping("/update-contact/{cId}")
public String updateContact(Model model,@PathVariable("cId")Integer cId) {
	Contact contact = this.contactRepository.findById(cId).get();
	model.addAttribute("contact",contact);
	model.addAttribute("title","Update Contact");
	return "normal/update_contact";
}

//handling updates
@PostMapping("/process-contact-update")
public String updateContactProcess(@ModelAttribute Contact contact,Model model,@RequestParam("profileImage")MultipartFile file,Principal principal,HttpSession session)
{
	Contact oldContactDetail = this.contactRepository.findById(contact.getcId()).get();
	try {
		//image
		if(!file.isEmpty())
		{
			//file work
			//rewrite the existing file
			
			//delete old photo
			File deleteFile = new ClassPathResource("static/images").getFile();
			File file2 = new File(deleteFile,oldContactDetail.getImage());
			file2.delete();
			//update new phoyo
			
			File saveFile = new ClassPathResource("static/images").getFile();
			String uniqueFileName = oldContactDetail.getEmail()+file.getOriginalFilename();
			System.out.println("uniquename------"+uniqueFileName);
			System.out.println("contact image email----------"+oldContactDetail.getEmail());
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+uniqueFileName);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			contact.setImage(uniqueFileName);
		}
		else {
			contact.setImage(oldContactDetail.getImage());
		}
		User user = this.userRepository.getUserByUserName(principal.getName());
		contact.setUser(user);
		this.contactRepository.save(contact);
		session.setAttribute("message",new message("Your Contact has been successfully updated ..!!", "alert-success"));
	} catch (Exception e) {
		// TODO: handle exception
	}
	System.out.println("contact id"+contact.getcId());
	return "redirect:/user/contact/"+contact.getcId();
}
@GetMapping("/profile")
public String profilePage(Model model,Principal principal) {
	String userName = principal.getName();
	   System.out.println("USERNAME"+userName);
	   //get the user by username(email)
	   User user = userRepository.getUserByUserName(userName);
	
	model.addAttribute("title",user.getName());
	model.addAttribute("user",user);
	return "normal/user_profile";
}
}
