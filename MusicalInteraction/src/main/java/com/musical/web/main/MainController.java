package com.musical.web.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
	
	@RequestMapping("/")
	public String main(){
		return "main";
	}
	
	@RequestMapping("/introduction")
	public String introduction(){
		return "introduction";
	}
	
	@RequestMapping("/error")
	public String error(){
		return "error";
	}
	
	
}
