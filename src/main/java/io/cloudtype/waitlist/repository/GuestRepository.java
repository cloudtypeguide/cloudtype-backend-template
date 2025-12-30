package io.cloudtype.waitlist.repository;

import io.cloudtype.waitlist.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    // 🔴 [추가] "A룸"의 "오늘 날짜" 예약만 몽땅 가져오는 명령어
    List<Guest> findByRoomNameAndDate(String roomName, String date);
}
