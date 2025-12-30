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

@CrossOrigin(origins = "*") // 프론트엔드 접속 허용
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

    // 2. 예약 생성 (POST)
    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        return guestRepository.save(guest);
    }

    // 3. 특정 예약 조회
    @GetMapping("/{id}")
    public ResponseEntity<Guest> getGuestById(@PathVariable Long id) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not exist with id :" + id));
        return ResponseEntity.ok(guest);
    }

    // 4. 예약 수정 (PUT) - 🚨 여기가 에러 났던 부분! 수정 완료
    @PutMapping("/{id}")
    public ResponseEntity<Guest> updateGuest(@PathVariable Long id, @RequestBody Guest guestInfo) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not exist with id :" + id));

        // 예전 코드: guest.setName(guestInfo.getName()); (X)
        // 바뀐 코드: 새 변수명으로 매핑 (O)
        guest.setDeptName(guestInfo.getDeptName());
        guest.setBookerName(guestInfo.getBookerName());
        guest.setRoomName(guestInfo.getRoomName());
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
