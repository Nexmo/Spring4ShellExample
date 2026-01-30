package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/*
 * @Controller - Marks this class as a Spring MVC controller.
 * 
 * This annotation enables Spring's request handling mechanism which 
 * includes automatic data binding. Without @Controller (or @RestController),
 * Spring wouldn't process the incoming requests and perform the 
 * vulnerable data binding operation.
 */

@Controller
public class GreetingController {

	@GetMapping("/greeting")
	public String greetingForm(Model model) {
		model.addAttribute("greeting", new Greeting());
		return "greeting";
	}

    /**
     * @PostMapping - Handles HTTP POST requests to /greeting
     * 
     * @ModelAttribute - THIS IS THE CRITICAL ANNOTATION
     * 
     * What @ModelAttribute does:
     * 1. Creates a new instance of Greeting (or uses existing from Model)
     * 2. Uses Spring's WebDataBinder to bind request parameters to the object
     * 3. The binder uses JavaBeans PropertyDescriptors to find setters
     * 
     * THE VULNERABILITY:
     * When binding parameters like:
     *   class.module.classLoader.resources.context.parent.pipeline.first.pattern=MALICIOUS
     * 
     * Spring's data binder interprets dots as nested property access:
     *   greeting.getClass()
     *       .getModule()           // JDK 9+ only - this is why JDK 9+ is required
     *       .getClassLoader()
     *       .getResources()
     *       .getContext()
     *       .getParent()
     *       .getPipeline()
     *       .getFirst()            // Returns AccessLogValve
     *       .setPattern(MALICIOUS) // Sets the log pattern!
     * 
     * By manipulating AccessLogValve properties, attackers can:
     *   - directory: Set where logs are written
     *   - prefix: Set the log filename
     *   - suffix: Set file extension (e.g., .jsp)
     *   - pattern: Set content written to log (e.g. JSP webshell code)
     *   - fileDateFormat: Trigger log file creation
	* 
	* IMPORTANT:
	* When a POJO is a method parameter without certain annotations,
     * Spring implicitly treats it as @ModelAttribute.
     */

	@PostMapping("/greeting")
	public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
		model.addAttribute("greeting", greeting);
		return "result";
	}

	/**
     * ========================================================================
     * WHY THESE ANNOTATIONS MATTER
     * ========================================================================
     * 
     * @Controller vs @RestController:
     * - Both are vulnerable if they accept POJOs
     * - The vulnerability is in data binding, not response handling
     * 
     * @ModelAttribute:
     * - Explicitly tells Spring to bind request params to this object
     * - Uses WebDataBinder internally
     * - Supports nested property notation (the key to the exploit)
     * 
     * @RequestParam:
     * - Binds individual parameters, NOT object graphs
     * - NOT vulnerable to this specific exploit
     * - Example: @RequestParam String name - only binds "name" param
     * 
     * @Valid / @Validated:
     * - Adds validation AFTER binding
     * - Does NOT prevent the exploit (binding happens first!)
     * - The malicious properties are already set before validation runs
     */

}