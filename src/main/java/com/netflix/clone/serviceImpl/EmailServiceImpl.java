package com.netflix.clone.serviceImpl;

import com.netflix.clone.exceptions.EmailNotVerifiedException;
import com.netflix.clone.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger= LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontEndUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        try{
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Verify your Email ");
            String emailBody = getString(token);
            message.setText(emailBody);
            javaMailSender.send(message);
            logger.info("Verification email sent to {}" ,toEmail);

        }catch(Exception ex){
            logger.error("Failed to send verification email {} :{}",toEmail,ex.getMessage(),ex);
            throw new EmailNotVerifiedException("Failed to send notification email");
        }
    }

    private String getString(String token) {
        String verificationLink=frontEndUrl+"/verify-email?token="+ token;
        String emailBody="Welcome to Netflix Cone!\n\n"
                    +"Thank you for registering. Please  verify your email address by click below \n\n"
                    +verificationLink
                    +"\n\nThis line will expire in 24 hours.\n\n"
                    +"If you didn't create this account, please ignore this email.\n\n"
                    +"Best Regards,\n"
                    +"Netflix Clone Team";
        return emailBody;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        try{
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Netflix Clone - Password Reset");
            String resetLink=frontEndUrl+"/reset-password?token="+token;
            String emailBody="Hi,\n\n"
                    +"We have received your request to reset your password.Click the link below to reset it \n\n"
                    +resetLink
                    +"This will expire in 1 hour. Thank you \n\n"
                    + "If didn't request password request, ignore this email "
                    +"Best Regards"
                    +"Netflix Clone Team";
            message.setText(emailBody);
            javaMailSender.send(message);
            logger.info ("Password reset email sent to :{}",toEmail);

        } catch (Exception ex) {
            logger.error("Failed to send reset password email {} :{}",toEmail,ex.getMessage(),ex);
            throw new RuntimeException("Failed to reset email");
        }

    }
}
