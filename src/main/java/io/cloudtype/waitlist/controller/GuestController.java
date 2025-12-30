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

// 🟢 [필수] 프론트엔드(React)에서의 접속을 허용합니다.
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

    // 2. 예약 생성 (POST) - 🔴 [핵심] 중복 검사 및 디버깅 로그 추가
    @PostMapping
    public Guest createGuest(@RequestBody Guest guest) {
        try {
            // 서버 로그에 들어온 데이터 찍어보기 (디버깅용)
            System.out.println("📥 예약 요청 수신: " + guest.getRoomName() + " / " + guest.getDate());
            System.out.println("🕒 시간 확인: " + guest.getStartTime() + " ~ " + guest.getEndTime());

            // (1) 해당 날짜, 해당 방의 기존 예약들을 가져옵니다.
            List<Guest> existingGuests = guestRepository.findByRoomNameAndDate(guest.getRoomName(), guest.getDate());

            // (2) 시간 중복 검사
            for (Guest existing : existingGuests) {
                // 로직: (새 예약 시작 < 기존 예약 종료) AND (새 예약 종료 > 기존 예약 시작)
                if (guest.getStartTime().compareTo(existing.getEndTime()) < 0 &&
                    guest.getEndTime().compareTo(existing.getStartTime()) > 0) {
                    
                    String errorMsg = "이미 예약된 시간입니다! (" + existing.getTimeInfo() + ")";
                    System.out.println("❌ 예약 거절됨: " + errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }

            // (3) 문제 없으면 저장
            Guest savedGuest = guestRepository.save(guest);
            System.out.println("✅ 예약 저장 완료: ID " + savedGuest.getId());
            return savedGuest;

        } catch (Exception e) {
            // 에러 발생 시 로그 출력
            System.out.println("❌ 서버 에러 발생: " + e.getMessage());
            e.printStackTrace
