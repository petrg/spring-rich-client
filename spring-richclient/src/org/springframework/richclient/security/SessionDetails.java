package org.springframework.richclient.security;

import java.io.Serializable;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.AuthenticationException;
import net.sf.acegisecurity.AuthenticationManager;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.SecureContext;
import net.sf.acegisecurity.context.SecureContextImpl;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.springframework.context.ApplicationContext;
import org.springframework.core.closure.Constraint;
import org.springframework.richclient.application.Application;
import org.springframework.rules.PropertyConstraintProvider;
import org.springframework.rules.Rules;
import org.springframework.rules.constraint.property.PropertyConstraint;

/**
 * JavaBean suitable for use with form model.
 * 
 * <P>
 * Temporarily stores the username and password provided by the user.
 * 
 * @author Ben Alex
 */
public class SessionDetails implements Serializable, PropertyConstraintProvider {
    public static final String PROPERTY_USERNAME = "username";

    public static final String PROPERTY_PASSWORD = "password";

    private transient AuthenticationManager authenticationManager;

    private String username;

    private String password;

    private Rules validationRules;

    public SessionDetails() {
        // Retrieve any existing login information from the
        // ContextHolder
        if (ContextHolder.getContext() instanceof SecureContext) {
            SecureContext sc = (SecureContext)ContextHolder.getContext();
            if (sc.getAuthentication() != null) {
                setUsername(sc.getAuthentication().getPrincipal().toString());
                setPassword(sc.getAuthentication().getCredentials().toString());
            }
        }
        initRules();
    }

    protected void initRules() {
        this.validationRules = new Rules(getClass()) {
            protected void initRules() {
                add(PROPERTY_USERNAME, all(new Constraint[] { required(), maxLength(getUsernameMaxLength()) }));
                add(PROPERTY_PASSWORD, all(new Constraint[] { required(), minLength(getPasswordMinLength()) }));
            }

            protected int getUsernameMaxLength() {
                return 8;
            }

            protected int getPasswordMinLength() {
                return 2;
            }

        };
    }

    public PropertyConstraint getPropertyConstraint(String propertyName) {
        return validationRules.getPropertyConstraint(propertyName);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthenticationManager(AuthenticationManager manager) {
        this.authenticationManager = manager;
    }

    protected Class getSecureContextClass() {
        return SecureContextImpl.class;
    }

    public void login() throws AuthenticationException {
        // Attempt login
        UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(getUsername(),
                getPassword());

        Authentication result = authenticationManager.authenticate(request);

        // Setup a secure ContextHolder (if required)
        if (ContextHolder.getContext() == null || !(ContextHolder.getContext() instanceof SecureContext)) {
            try {
                ContextHolder.setContext((SecureContext)getSecureContextClass().newInstance());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // Commit the successful Authentication object to the secure
        // ContextHolder
        SecureContext sc = (SecureContext)ContextHolder.getContext();
        sc.setAuthentication(result);
        ContextHolder.setContext(sc);

        // Fire application event to advise of new login
        ApplicationContext appCtx = Application.services().getApplicationContext();
        appCtx.publishEvent(new LoginEvent(result));
    }

    public static Authentication logout() {
        Authentication existing = null;

        // Make the Authentication object null if a SecureContext exists
        if (ContextHolder.getContext() != null && ContextHolder.getContext() instanceof SecureContext) {
            SecureContext sc = (SecureContext)ContextHolder.getContext();
            existing = sc.getAuthentication();
            sc.setAuthentication(null);
            ContextHolder.setContext(sc);
        }

        // Create a non-null Authentication object if required (to meet
        // ApplicationEvent contract)
        if (existing == null) {
            existing = LogoutEvent.NO_AUTHENTICATION;
        }

        // Fire application event to advise of logout
        ApplicationContext appCtx = Application.services().getApplicationContext();
        appCtx.publishEvent(new LogoutEvent(existing));

        return existing;
    }

}