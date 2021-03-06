package backend;

import calendar.CalendarItem;

import com.sun.mail.smtp.SMTPTransport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author doraemon
 */
public class Mailer {
	
	private static final String RECEIVER = "bitbv2014@gmail.com"; //recipient of mail
	private static final String NL = "<br />" ; //Line separator
	private static final String LINE = "-----------------------------------------"; //Horizontal line
    
	String username;
	String password;
	
	Properties props = new Properties();
	
	public Mailer(String properties) {
		this.loadProperties(properties);
    }

    /**
     * Send email using GMail SMTP server.
     *
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param title title of the message
     * @param message message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
    private void Send(String recipientEmail, String title, String message) {
        this.Send(recipientEmail, "", title, message);
    }

    /**
     * Send email using GMail SMTP server.
     *
     * @param username GMail username
     * @param password GMail password
     * @param recipientEmail TO recipient
     * @param ccEmail CC recipient. Can be empty if there is no CC recipient
     * @param title title of the message
     * @param message message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
    private void Send(String recipientEmail, String ccEmail, String title, String message) {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        try {
			msg.setFrom(new InternetAddress(username + "@gmail.com"));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

	        if (ccEmail.length() > 0) {
	            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
	        }

	        msg.setSubject(title);
	        //msg.setText(message, "utf-8");
	        msg.setContent(message, "text/html; charset=utf-8");
	        msg.setSentDate(new Date());

	        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

	        t.connect("smtp.gmail.com", this.username, this.password);
	        t.sendMessage(msg, msg.getAllRecipients());      
	        t.close();
		} catch (AddressException e) {
			System.err.println("Adres email error: " + e.getMessage());
		} catch (MessagingException e) {
			System.err.println("message email error: " + e.getMessage());
		}
        
    }
    
    /**
     * Laad de connectie eigenschappen uit een properties bestand
     */
    private void loadProperties(String prop){
    	try {
    		FileInputStream in = new FileInputStream(prop);
            props.load(in);
            
            username = props.getProperty("gmail.user");
            password = props.getProperty("gmail.pass");

        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());

        } catch (IOException e){
        	System.err.println(e.getMessage());
        }
    }
    
    /**
     * Stuurt een mail met daarin de gegevens van een container
     * @param item
     */
    public void composeMail(CalendarItem item) {
    	composeMail(item,null);
    } 
    
    /**
     * Stuurt een mail met daarin de gegevens van een container
     * @param item
     */
    public void composeMail(CalendarItem item, String additionalInfo) {
    	String msg = "";
    	
    	if (additionalInfo != null) {
	    	msg += "Automatisch gegenereerde foutmelding: " + NL;
	    	msg += additionalInfo;
	    	msg += NL + NL + LINE + NL + NL;
    	}
    	
    	msg += "Container-informatie: " + NL;
    	msg += item.toMailString();
    	
    	
    	
    	String title = "[TIMBAL-AUTOMAILER]: " + item.getContainernr();
    	
    	this.Send(RECEIVER, title, msg);
    }
}