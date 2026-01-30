package com.example.demo;

/**
 * Simple POJO (Plain Old Java Object) used for form data binding.
 * 
 * CVE-2022-22965 EXPLANATION:
 * ===========================
 * 
 * When Spring binds request parameters to this object, it uses 
 * JavaBeans introspection to find setters. The problem is that 
 * ALL Java objects inherit from Object, which has a getClass() method.
 * 
 * In JDK 9+, the Class object provides access to:
 *   object.getClass().getModule().getClassLoader()
 * 
 * This means an attacker can traverse the object graph:
 *   class.module.classLoader.resources.context.parent.pipeline.first
 * 
 * This path reaches Tomcat's AccessLogValve, allowing attackers to:
 *   1. Set the log directory (e.g., webapps/ROOT)
 *   2. Set the log filename suffix (e.g., .jsp)
 *   3. Set the log pattern (containing JSP webshell code)
 *   4. Trigger log rotation to write the malicious file
 * 
 * The attack chain:
 *   greetings -> getClass() -> Class
 *        -> getModule() -> Module (JDK 9+ only!)
 *        -> getClassLoader() -> WebappClassLoaderBase
 *        -> getResources() -> WebResourceRoot
 *        -> getContext() -> StandardContext
 *        -> getParent() -> StandardHost
 *        -> getPipeline() -> StandardPipeline
 *        -> getFirst() -> AccessLogValve
 * 
 * HOW JAVA SERIALIZATION/DATA BINDING RELATES TO THE VULNERABILITY:
 * -----------------------------------------------------------------
 * 
 * This is a Plain Old Java Object (POJO) used for Spring's data binding.
 * Spring's WebDataBinder automatically maps HTTP request parameters to
 * object properties using JavaBean conventions (getters/setters).
 * 
 * WHY JDK 9+ IS REQUIRED:
 * -----------------------
 * Before JDK 9, Class.getClassLoader() returned different ClassLoader types
 * that didn't expose this property chain. The Module system in JDK 9+
 * introduced getModule() which provides the exploitable path.
 */


public class Greeting {

	private String content;

    /*
     * 
     * For parameter "name=John":
     *   - WebDataBinder calls setName("John")
     * 
     * For parameter "class.module.classLoader...":
     *   - WebDataBinder calls getClass(), then follows the chain
     *   - At the end, it calls a setter on the final object
	 * 
	 * getClass() method is:
     *   - PUBLIC: So Spring's reflection can access it
     *   - Returns an object: So nested property traversal works
     *   - Present on ALL objects: Every POJO is vulnerable
     * 
     * The attack exploits this implicit property to reach Tomcat internals.
     */

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

