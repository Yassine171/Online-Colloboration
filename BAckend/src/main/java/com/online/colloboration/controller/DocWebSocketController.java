package com.online.colloboration.controller;


import com.online.colloboration.models.Doc;
import com.online.colloboration.models.DocumentMessage;
import com.online.colloboration.services.DocService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import org.springframework.messaging.handler.annotation.DestinationVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DocWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final DocService docService;


    @MessageMapping("/document/{documentId}")
    @SendTo("/topic/document/{documentId}")
    public void handleDocumentMessage(@DestinationVariable Long documentId, @Payload DocumentMessage documentMessage) throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal principal = (Principal) authentication.getPrincipal();

        Doc document = docService.getDocById(documentId);

        if (document == null) {
            throw new RuntimeException();
        }

        // Check if the user is authorized to edit the document
        if (!document.getOwner().getUsername().equals(principal.getName()) &&
                !document.getSharedWith().stream().anyMatch(user -> user.getUsername().equals(principal.getName()))) {
            throw new AccessDeniedException("Unauthorized");
        }

        // Update the document with the message content
        document.setContent(documentMessage.getContent());
        docService.saveDoc(document);

        // Send the updated document to all connected clients
        messagingTemplate.convertAndSend("/topic/document/" + documentId, documentMessage.getDelta());
    }
}
