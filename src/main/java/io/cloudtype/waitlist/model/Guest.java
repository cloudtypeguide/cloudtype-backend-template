package io.cloudtype.waitlist.model;

import lombok.*;
import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String deptName;

    @Column(nullable = false)
    private String bookerName;

    @Column(nullable = false)
    private String roomName;

    // 🔴 [추가] 중복 검사용 데이터 (예: "2024-12-30")
    @Column(nullable = false)
    private String date;

    // 🔴 [추가] 중복 검사용 데이터 (예: "09:30")
    @Column(nullable = false)
    private String startTime;

    // 🔴 [추가] 중복 검사용 데이터 (예: "10:30")
    @Column(nullable = false)
    private String endTime;

    // 화면에 보여주기용 예쁜 문자열 (예: "2024... (09:30 ~ 10:30)")
    @Column(nullable = false)
    private String timeInfo;
}
