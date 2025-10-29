/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.service.groovy;

//import com.google.cloud.Binding;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class GroovyScriptService {

    private GroovyScriptEngine groovyScriptEngine;

    public GroovyScriptService() throws Exception {
        // Point to your groovy scripts directory
        groovyScriptEngine = new GroovyScriptEngine("src/main/groovy");
    }

    public Object runScript(String scriptName, Map<String, Object> variables) {
        try {
            Binding binding = new Binding();
            variables.forEach(binding::setVariable);
            return groovyScriptEngine.run(scriptName, binding);
        } catch (ResourceException | ScriptException ex) {

        }

        return null;
    }
}
