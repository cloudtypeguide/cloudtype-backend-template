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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/guests")
public class GuestController {

    @Autowired
    private GuestRepository guestRepository;

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    // 🔴 [수정됨] 반환 타입을 Guest -> ResponseEntity<?> 로 변경하여 유연하게 대처
    @PostMapping
    public ResponseEntity<?> createGuest(@RequestBody Guest guest) {
        try {
            System.out.println("📥 예약 요청: " + guest.getRoomName() + " / " + guest.getDate());

            List<Guest> existingGuests = guestRepository.findByRoomNameAndDate(guest.getRoomName(), guest.getDate());

            for (Guest existing : existingGuests) {
                if (guest.getStartTime().compareTo(existing.getEndTime()) < 0 &&
                    guest.getEndTime().compareTo(existing.getStartTime()) > 0) {
                    
                    // 🚨 [핵심 변경] 에러가 나면 500 JSON 대신, 400 상태코드와 '문자열 메시지'만 보냅니다.
                    return ResponseEntity
                        .badRequest()
                        .body("이미 예약된 시간입니다! (" + existing.getTimeInfo() + ")");
                }
            }

            Guest savedGuest = guestRepository.save(guest);
            return ResponseEntity.ok(savedGuest);

        } catch (Exception e) {
            e.printStackTrace();
            // 그 외 알 수 없는 에러는 500으로 처리
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not exist with id :" + id));
        return ResponseEntity.ok(guest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guestInfo) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not exist with id :" + id));

        guest.setDeptName(guestInfo.getDeptName());
        guest.setBookerName(guestInfo.getBookerName());
        guest.setRoomName(guestInfo.getRoomName());
        guest.setDate(guestInfo.getDate());
        guest.setStartTime(guestInfo.getStartTime());
        guest.setEndTime(guestInfo.getEndTime());
        guest.setTimeInfo(guestInfo.getTimeInfo());

        Guest updatedGuest = guestRepository.save(guest);
        return ResponseEntity.ok(updatedGuest);
    }

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
