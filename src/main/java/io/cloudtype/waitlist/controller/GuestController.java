package io.cloudtype.waitlist.controller;

import io.cloudtype.waitlist.exception.ResourceNotFoundException;
import io.cloudtype.waitlist.model.Guest;
import io.cloudtype.waitlist.repository.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 🔴 [중요] 프론트엔드(React)에서 보내는 요청을 허용하는 설정입니다. 절대 지우지 마세요.
@CrossOrigin(origins = "*") 
@RestController
@RequestMapping("/api/guests")
public class GuestController {

    @Autowired
    private GuestRepository guestRepository;

    // 1. 모든 예약 목록 조회
    @GetMapping
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    // 2. 예약 생성 (POST) - 🔴 [핵심 기능] 중복 검사 로직 추가됨
    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        
        // (1) 같은 방, 같은 날짜에 예약된 리스트를 가져옵니다.
        // 주의: GuestRepository에 findByRoomNameAndDate 메소드가 있어야 합니다.
        List<Guest> existingGuests = guestRepository.findByRoomNameAndDate(guest.getRoomName(), guest.getDate());

        // (2) 가져온 예약들과 시간을 비교합니다.
        for (Guest existing : existingGuests) {
            // 로직: (새 예약 시작 < 기존 예약 종료) AND (새 예약 종료 > 기존 예약 시작)
            // 이 조건이 참이면 시간이 겹치는 것입니다.
            if (guest.getStartTime().compareTo(existing.getEndTime()) < 0 &&
                guest.getEndTime().compareTo(existing.getStartTime()) > 0) {
                
                // 겹치면 에러를 발생시켜서 저장을 막습니다. (프론트엔드에서 alert창 뜸)
                throw new RuntimeException("이미 예약된 시간입니다! (" + existing.getTimeInfo() + ")");
            }
        }

        // (3) 검사를 통과하면 저장합니다.
        return guestRepository.save(guest);
    }

    // 3. 특정 예약 조회
    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not exist with id :" + id));
        return ResponseEntity.ok(guest);
    }

    // 4. 예약 수정 (PUT) - 🔴 [수정] 시간 정보 필드 업데이트 추가
    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guestInfo) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not exist with id :" + id));

        // 기본 정보 수정
        guest.setDeptName(guestInfo.getDeptName());
        guest.setBookerName(guestInfo.getBookerName());
        guest.setRoomName(guestInfo.getRoomName());
        
        // 🔴 [중요] 날짜와 시간 정보도 같이 수정해줘야 합니다.
        guest.setDate(guestInfo.getDate());
        guest.setStartTime(guestInfo.getStartTime());
        guest.setEndTime(guestInfo.getEndTime());
        guest.setTimeInfo(guestInfo.getTimeInfo());

        Guest updatedGuest = guestRepository.save(guest);
        return ResponseEntity.ok(updatedGuest);
    }

    // 5. 예약 삭제 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteGuest(@PathVariable Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not exist with id :" + id));

        guestRepository.delete(guest);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
