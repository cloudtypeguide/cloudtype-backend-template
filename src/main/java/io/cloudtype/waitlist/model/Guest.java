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

    // 1. 부서명 (예: 개발팀)
    @Column(nullable = false)
    private String deptName;

    // 2. 예약자 성함 (예: 홍길동)
    @Column(nullable = false)
    private String bookerName;

    // 3. 회의실 이름 (예: Creative Lab)
    @Column(nullable = false)
    private String roomName;

    // 4. 예약 시간 (예: 14:00 ~ 16:00)
    @Column(nullable = false)
    private String timeInfo;
}
