package net.ironoc.message.domain;

import org.springframework.messaging.Message;

public class UserMessageResp {
    private String username;
    private String targetName;
    private String content;
    private String sender;
    private String senderSessionId;
    private Message<Object> bootMessage;

    public Message<Object> getBootMessage() {
        return bootMessage;
    }

    public void setBootMessage(Message<Object> bootMessage) {
        this.bootMessage = bootMessage;
    }

    public String getSenderSessionId() {
        return senderSessionId;
    }

    public void setSenderSessionId(String senderSessionId) {
        this.senderSessionId = senderSessionId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "bootMessage=" + bootMessage +
                ", username='" + username + '\'' +
                ", targetName='" + targetName + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", senderSessionId='" + senderSessionId + '\'' +
                '}';
    }
}

