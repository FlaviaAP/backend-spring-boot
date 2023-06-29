package com.backendabstractmodel.demo.services.integration;

import com.backendabstractmodel.demo.enumeration.MessageEnum;
import com.backendabstractmodel.demo.exceptions.UnexpectedException;
import com.backendabstractmodel.demo.model.Email;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

@Slf4j
@Service
public class EmailIntegrationService {

    @Value("${spring.mail.username}")
    private String gmail;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Configuration fmConfiguration;


    public void send(Email email, String textTemplate) {
        String emailTo = null;
        try {
            emailTo = email.getTo();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(gmail);
            mimeMessageHelper.setTo(emailTo);
            mimeMessageHelper.setSubject(email.getSubject());

            mimeMessageHelper.setText(
                getContentFromTemplateText(email.getArgs(), textTemplate),
                true
            );

            mailSender.send(mimeMessageHelper.getMimeMessage());
            log.info("Email sent success: subject " + email.getSubject() + ", to " + emailTo);
        }
        catch (Exception e) {
            log.error("Error at sending email to {}", emailTo);
            throw new UnexpectedException(MessageEnum.EMAIL_SEND_ERROR, e);
        }
    }

    private String getContentFromTemplateText(Map<String, Object> args, String templateText) throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(
            new Template("t", new StringReader(templateText), fmConfiguration),
            args
        );
    }

}
