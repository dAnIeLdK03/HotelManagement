package com.hotelsystemmanegment.Service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender emailSender;

    public void sendReservationConfirmation(String to, String name, String confirmationCode, String checkInDate, String checkOutDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Hotel Reservation Confirmation");
        message.setText("Dear " + name + ",\n\n" +
                "Thank you for booking with us!\n" +
                "Your reservation is confirmed. Here are your details:\n" +
                "Confirmation Code: " + confirmationCode + "\n" +
                "Check-in Date: " + checkInDate + "\n" +
                "Check-out Date: " + checkOutDate + "\n\n" +
                "We look forward to welcoming you!\n\n" +
                "Best regards,\n" +
                "The Hotel Team");
        emailSender.send(message);
    }

    public void sendFeedbackRequest(@NotBlank(message = "email is required") String email, @NotBlank(message = "name is required") String name, String bookingConfirmationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Вашето мнение за престоя е важно за нас!");
        message.setText(String.format(
                "Здравейте, %s!\n\nБлагодарим Ви, че избрахте нашия хотел. Ще се радваме да споделите мнението си за престоя:\n" +
                        "http://localhost:3000/feedback?bookingCode=%s\n\nС уважение,\nЕкипът на хотела",
                name, bookingConfirmationCode
        ));
        emailSender.send(message);
    }

    public void sendFeedbackRequestEmail(String to, String name, String confirmationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("We Hope You Enjoyed Your Stay!");
        message.setText("Dear " + name + ",\n\n" +
                "Thank you for staying with us. We hope you had a wonderful experience.\n\n" +
                "We would be grateful if you could take a moment to share your feedback about your stay (Booking: " + confirmationCode + "). " +
                "Your opinion is very important to us and helps us improve our services.\n\n" +
                // In a real application, you would include a link to a feedback form here.
                "To leave a review, please visit our website.\n\n" +
                "Thank you for your time!\n\n" +
                "Best regards,\n" +
                "The Hotel Team");
        emailSender.send(message);
    }
} 