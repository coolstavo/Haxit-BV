package com.webapp.hexit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @ManyToOne
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private LocalDateTime sentAt;

  public Message() {}

  public Message(
    User sender,
    User receiver,
    String content,
    LocalDateTime sentAt
  ) {
    this.sender = sender;
    this.receiver = receiver;
    this.content = content;
    this.sentAt = sentAt;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public User getReceiver() {
    return receiver;
  }

  public void setReceiver(User receiver) {
    this.receiver = receiver;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(LocalDateTime sentAt) {
    this.sentAt = sentAt;
  }
}
