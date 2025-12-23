package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.reflect.Method;

// BROKEN - TO BE FIXED!!!

/**
 * Shows the Spring4Shell property chain in plain text.
 * Access: GET /debug/chain
 */
@RestController
public class ChainController {

    @GetMapping(value = "/debug/chain", produces = "text/plain")
    public String showChain() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Spring4Shell Property Chain ===\n\n");
        
        Object current = new Object(); // Start: any POJO
        
        // Step 1: object -> Class
        sb.append("1. object.getClass()\n");
        sb.append("   Returns: ").append(current.getClass()).append("\n\n");
        Class<?> clazz = current.getClass();
        
        // Step 2: Class -> Module (JDK 9+ only)
        sb.append("2. class.getModule()\n");
        Module module = clazz.getModule();
        sb.append("   Returns: ").append(module).append("\n\n");
        
        // Step 3: Module -> ClassLoader
        sb.append("3. module.getClassLoader()\n");
        ClassLoader loader = module.getClassLoader();
        sb.append("   Returns: ").append(loader.getClass().getName()).append("\n\n");
        
        // Step 4-8: Traverse Tomcat internals
        try {
            Object resources = invoke(loader, "getResources");
            sb.append("4. classLoader.getResources()\n");
            sb.append("   Returns: ").append(className(resources)).append("\n\n");
            
            Object context = invoke(resources, "getContext");
            sb.append("5. resources.getContext()\n");
            sb.append("   Returns: ").append(className(context)).append("\n\n");
            
            Object parent = invoke(context, "getParent");
            sb.append("6. context.getParent()\n");
            sb.append("   Returns: ").append(className(parent)).append("\n\n");
            
            Object pipeline = invoke(parent, "getPipeline");
            sb.append("7. parent.getPipeline()  <-- THE PIPELINE\n");
            sb.append("   Returns: ").append(className(pipeline)).append("\n");
            sb.append("   Pipeline = chain of Valves that process requests\n\n");
            
            Object first = invoke(pipeline, "getFirst");
            sb.append("8. pipeline.getFirst()  <-- TARGET!\n");
            sb.append("   Returns: ").append(className(first)).append("\n");
            sb.append("   First = first Valve, often AccessLogValve\n\n");
            
            // Show AccessLogValve properties if found
            if (first != null && className(first).contains("AccessLog")) {
                sb.append("=== AccessLogValve Current Settings ===\n");
                sb.append("   directory: ").append(invoke(first, "getDirectory")).append("\n");
                sb.append("   prefix:    ").append(invoke(first, "getPrefix")).append("\n");
                sb.append("   suffix:    ").append(invoke(first, "getSuffix")).append("\n");
                sb.append("   pattern:   ").append(invoke(first, "getPattern")).append("\n\n");
                
                sb.append("=== Exploit modifies these to: ===\n");
                sb.append("   directory: webapps/ROOT\n");
                sb.append("   prefix:    shell\n");
                sb.append("   suffix:    .jsp\n");
                sb.append("   pattern:   <%=Runtime.getRuntime().exec(cmd)%>\n");
            }
            
        } catch (Exception e) {
            sb.append("Chain traversal stopped: ").append(e.getMessage()).append("\n");
            sb.append("(Full chain only visible when deployed on Tomcat as WAR)\n");
        }
        
        return sb.toString();
    }
    
    private Object invoke(Object obj, String method) throws Exception {
        if (obj == null) return null;
        Method m = obj.getClass().getMethod(method);
        return m.invoke(obj);
    }
    
    private String className(Object obj) {
        return obj == null ? "null" : obj.getClass().getName();
    }
}