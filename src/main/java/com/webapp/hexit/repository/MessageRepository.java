package com.webapp.hexit.repository;

import com.webapp.hexit.model.Message;
import com.webapp.hexit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>
{
    @Query("""
      select m from Message m
      where (m.sender = :a and m.receiver = :b)
         or (m.sender = :b and m.receiver = :a)
      order by m.sentAt asc
    """)
    List<Message> findConversation(@Param("a") User a, @Param("b") User b);

    @Query("""
      select distinct
        case
          when m.sender = :me then m.receiver.username
          else m.sender.username
        end
      from Message m
      where m.sender = :me or m.receiver = :me
      order by 1 asc
    """)
    List<String> findChatPartnersUsernames(@Param("me") User me);

    long countByReceiverAndReadAtIsNull(User receiver);

    long countBySenderAndReceiverAndReadAtIsNull(User sender, User receiver);

    @Modifying
    @Transactional
    @Query("""
      update Message m
      set m.readAt = :now
      where m.sender = :other
        and m.receiver = :me
        and m.readAt is null
      """)
    int markReadFromOther(@Param("me") User me,
                          @Param("other") User other,
                          @Param("now") LocalDateTime now);
}
